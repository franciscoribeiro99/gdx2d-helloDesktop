
import ch.hevs.gdx2d.components.bitmaps.{BitmapImage, Spritesheet}
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable.ArrayBuffer

class Graphics extends PortableApplication(1920, 1080) {
  //levelManager
  var levelManager: Levels = new Levels
  var levelPlaying = false

  //gameState 0-home menu 1-playing 2-next level 3-lost 4-finish
  var gameState = 0


  var players = 0


  // ArrayBuffer of objects
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val bullets: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()
  var buttonsList = new ArrayBuffer[ClickableButton]()
  var playerList = new ArrayBuffer[Player]()


  //allButtons
  var exitButton: ClickableButton = _
  var nextLevel: ClickableButton = _
  var player1: ClickableButton = _
  var player2: ClickableButton = _
  var restart: ClickableButton = _

  //time
  var time = new Time
  //var elapsedTime: Float = 30
  var rightKeyPressed = false
  var leftKeyPressed = false
  // ArrayBuffer to remove objects
  val ballsToAdd: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val ballsToRemove: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  // Create an ArrayBuffer to store bullets that need to be removed
  val bulletsToRemove: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()

  // Physics
  val world: World = PhysicsWorld.getInstance()
  world.setGravity(new Vector2(0, -1.2f)) // Update the gravity value as per your needs
  var dbg: DebugRenderer = null
  var start = false


  //backgroiund
  var initialBackground: BitmapImage = _

  def initializeGameState(): Unit = {
    // Initialize game state components

    levelManager.levelRst()
    gameState = 0


    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
    dbg = new DebugRenderer()
    balls.clear()
    ballsToAdd.clear()
    ballsToRemove.clear()
    bullets.clear()
    bulletsToRemove.clear()
    println(levelManager.level)
    start = false
  }


  override def onInit(): Unit = {
    setTitle("BubbleTrouble")
    initializeGameState()




    //allButtons
    restart = new ClickableButton("Restart", new BitmapImage("data/images/buttons/Restart.png"), getWindowWidth / 2, 3 * (getWindowHeight / 4))
    exitButton = new ClickableButton("Exit", new BitmapImage("data/images/buttons/Exit.png"), getWindowWidth / 2, (getWindowHeight / 4))
    nextLevel = new ClickableButton("NextLevel", new BitmapImage("data/images/buttons/NextLevel.png"), getWindowWidth / 2, getWindowHeight / 2)
    player1 = new ClickableButton("Player1", new BitmapImage("data/images/buttons/Player1.png"), getWindowWidth / 2, 3 * (getWindowHeight / 4))
    player2 = new ClickableButton("Player2", new BitmapImage("data/images/buttons/Player2.png"), getWindowWidth / 2, 2 * (getWindowHeight / 4))

    //background
    initialBackground = new BitmapImage("data/images/brick-wall-background-texture.jpg")

  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    gameState match {
      case 3 => //lost
        time.elapsedTime = 30
        val img = new BitmapImage("data/images/backgroundfin.jpg")
        g.drawBackground(img, 0, 0)
        exitButton.draw(g)
        restart.draw(g)
        for (ball <- balls) {
          destroyBall(ball)
        }
      case 4 =>
        for (ball <- balls) {
          destroyBall(ball)
        }
        balls.clear()
        playerList.clear()
        g.drawBackground(initialBackground, 0, 0)
        exitButton.draw(g)
        nextLevel.draw(g)
      case 0 => //lobby
        g.clear()
        g.drawBackground(initialBackground, 0, 0)
        buttonsList.addOne(player1)
        buttonsList.addOne(player2)
        buttonsList.addOne(exitButton)
        for (b <- buttonsList) {
          b.draw(g)
        }
      case 1 => //playing
        buttonsList.clear()
        g.clear()
        g.drawFPS()
        g.drawSchoolLogo()

        if (players == 2) {
          playerList += new Player(960)
          playerList(0).ss = new Spritesheet("data/images/lumberjack_sheet.png", playerList(0).SPRITE_WIDTH, playerList(0).SPRITE_HEIGHT)
          playerList(0).draw(g)
          playerList += new Player(1440)
          playerList(1).ss == new Spritesheet("data/images/lumberjack_sheet.png", playerList(1).SPRITE_WIDTH, playerList(1).SPRITE_HEIGHT)
          playerList(1).draw(g)
        }
        else {
          playerList += new Player(960)
          playerList(0).ss = new Spritesheet("data/images/lumberjack_sheet.png", playerList(0).SPRITE_WIDTH, playerList(0).SPRITE_HEIGHT)
          playerList(0).draw(g)
        }

        if (rightKeyPressed) {
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).POSX += 10
          }
        }
        if (leftKeyPressed) {
          if (playerList(0).POSX > 0) {
            playerList(0).POSX -= 10
          }
        }
        time.elapsedTime -= Gdx.graphics.getDeltaTime
        g.drawString(60, 1050, s"Time: ${time.elapsedTime.toInt}", Align.right)
        g.drawString(1890, 1050, s"Level: ${levelManager.level}/6", Align.right)
        if (levelManager.level != 0 && levelPlaying == false) {
          levelPlaying = true
          if (levelManager.balls == 1) {
            val newBall = new Ball("Ball", levelManager.position1, levelManager.size)
            balls += newBall
          }
          else if (levelManager.balls == 2) {
            levelPlaying = true
            balls += new Ball("Ball1", levelManager.position1, levelManager.size)
            balls += new Ball("Ball2", levelManager.position2, levelManager.size)
          }
        }
        for (b <- balls) {
          b.draw(g)
          b.enableCollisionListener()
          if (b.checkCollisioWithPlayer(playerList(0))) {
            start = true
          }
          for (bullet <- bullets) {
            if (b.checkCollisionWithBullet(bullet)) {
              time.addTime(time.elapsedTime)
              b.destroy()
              if (b.radius == 16) {
                val bonus = new BonusTime(playerList(0).POSX, playerList(0).POSY)
                bonus.onGraphicRender(g)
                ballsToRemove += b
                bulletsToRemove += bullet
              }
              else {


                val ball1 = new Ball("Ball", new Vector2(b.ballBounds.x + 10, b.ballBounds.y), b.radius / 2)
                val ball2 = new Ball("Ball", new Vector2(b.ballBounds.x - 10, b.ballBounds.y), b.radius / 2)

                // Calcul late velocities for the new balls
                val angle1 = b.getBodyAngle + 45
                val angle2 = b.getBodyAngle + 105

                val velocity1 = new Vector2(MathUtils.cosDeg(angle1), MathUtils.sinDeg(angle1))
                val velocity2 = new Vector2(MathUtils.cosDeg(angle2), MathUtils.sinDeg(angle2))

                ball1.setBodyLinearVelocity(velocity1)
                ball2.setBodyLinearVelocity(velocity2)

                ballsToAdd += ball1
                ballsToAdd += ball2
                ballsToRemove += b
                bulletsToRemove += bullet
              }
            }
            else if (bullet.updateLine() == false)
              bulletsToRemove += bullet
          }
        }


        if (!ballsToRemove.isEmpty)
          balls --= ballsToRemove

        if (!ballsToAdd.isEmpty)
          balls ++= ballsToAdd

        for (bullet <- bullets) {
          if (!bullet.updateLine()) {
            bulletsToRemove += bullet
          } else {
            bullet.draw(g)
          }
        }

        if (!bulletsToRemove.isEmpty)
          bullets --= bulletsToRemove

        if (balls.isEmpty)
          levelPlaying = false


        //clear list to add and remove
        ballsToRemove.clear()
        ballsToAdd.clear()
        bulletsToRemove.clear()


        dbg.render(world, g.getCamera.combined)
        PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

        if (start || time.elapsedTime <= 0) {
          gameState = 3
        }
        else if (gameState == 1 && levelPlaying == false) {
          gameState = 4
        }
        if (levelManager.level == 7) {
          val img = new BitmapImage("data/images/youwin.png")
          g.drawBackground(img, 10f, 10f)
        }
    }


  }


  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    if (levelPlaying) {
      keycode match {
        case Input.Keys.SPACE =>
          if (bullets.isEmpty) {
            val newBullet = new Bullet("Bullet", MyPoint2D(playerList(0).POSX + (playerList(0).SPRITE_WIDTH / 2), playerList(0).POSY))
            bullets += newBullet
            Logger.log("New bullet created")
          }
        case Input.Keys.RIGHT =>
          playerList(0).textureY = 2
          rightKeyPressed = false
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.LEFT =>
          playerList(0).textureY = 1
          leftKeyPressed = false
          if (playerList(0).POSX > 0) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.ENTER =>
          if (start)
            initializeGameState()
        case _ => playerList(0).textureY = 0
      }
    }
  }

  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)
    println(s"clicked on $x $y $button")
    if (player1.click(x, y)&&gameState==0) {
      levelManager.levelUp()
      players = 1
      gameState = 1
      buttonsList.clear()
    }
    else if (player2.click(x, y) && gameState == 0) {
      levelManager.levelUp()
      players = 2
      gameState = 1
      buttonsList.clear()
    }
    else if (restart.click(x, y)) {
      start = !start
      gameState = 1
      buttonsList.clear()
    }
    else if (exitButton.click(x, y)) {
      System.exit(-1)
    }
    else if (nextLevel.click(x, y)) {
      levelManager.levelUp()
      gameState=1
      buttonsList.clear()
    }


  }


  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    if (gameState == 1) {
      keycode match {
        case Input.Keys.SPACE =>
          if (bullets.isEmpty) {
            val newBullet = new Bullet("Bullet", MyPoint2D(playerList(0).POSX + (playerList(0).SPRITE_WIDTH / 2), playerList(0).POSY))
            bullets += newBullet
            Logger.log("New bullet created")
          }
        case Input.Keys.RIGHT =>
          playerList(0).textureY = 2
          rightKeyPressed = true
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.LEFT =>
          playerList(0).textureY = 1
          leftKeyPressed = true
          if (playerList(0).POSX > 0) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.ENTER =>
          if (start)
            initializeGameState()
        case _ => playerList(0).textureY = 0
      }
    }

  }

  def destroyBall(ball: Ball): Unit = {
    world.destroyBody(ball.getBody)
    ball.destroy()
  }
}


object Test {
  def main(args: Array[String]): Unit = {
    new Graphics
  }
}
