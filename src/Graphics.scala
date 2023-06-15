
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
  var whereplaying = false

  var players = 0
  //BONUS
  var bonus = false
  var getballx = 0f
  var getbally = 0f

  // ArrayBuffer of objects
  val ballList: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val roapList: Array[Roap] = new Array[Roap](2)
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
  var rightKeyPressed1 = false
  var leftKeyPressed1 = false
  var rightKeyPressed2 = false
  var leftKeyPressed2 = false
  // ArrayBuffer to remove objects
  val ballsToAdd: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val ballsToRemove: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  var leftwall: Wall = _
  var rightwall: Wall = _


  // Physics
  val world: World = PhysicsWorld.getInstance()
  world.setGravity(new Vector2(0, -1.2f)) // Update the gravity value as per your needs
  var dbg: DebugRenderer = null
  var start = false


  //backgroiund
  var initialBackground: BitmapImage = _
  var finBackground: BitmapImage = _

  def initializeGameState(): Unit = {
    // Initialize game state components
    levelManager.levelRst()
    gameState = 0
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
    dbg = new DebugRenderer()
    bonus = false
    ballList.clear()
    ballsToAdd.clear()
    ballsToRemove.clear()
    roapList(0) = null
    roapList(1) = null
    println(levelManager.level)
    initialiseWalls()
    start = false
  }

  def initialiseWalls(): Unit = {
    leftwall = new Wall("wallLeft", new Vector2(0, getWindowHeight / 2), 10, getWindowHeight)
    rightwall = new Wall("wallight", new Vector2(getWindowWidth, getWindowHeight / 2), 10, getWindowHeight)
  }

  def removeWalls(): Unit = {
    leftwall.destroy()
    rightwall.destroy()
    leftwall = null
    rightwall = null
  }

  def createPlayers(): Unit = {
    if (players == 2) {
      playerList.insert(0, new Player(960))
      playerList.insert(1, new Player(1440))
    }
    else {
      playerList.insert(0, new Player(960))
    }
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
    finBackground = new BitmapImage("data/images/youwin.png")


  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    println(s"ball siye is: ${levelManager.size} and level is ${levelManager.level}")
    //println(s"the level is ${levelManager.level} and there is $players players")
    println(gameState)
    gameState match {
      case 2 => //finishGame
        g.drawBackground(finBackground, 0, 0)
        exitButton.draw(g)
        restart.draw(g)
        if (!ballList.isEmpty) {
          for (ball <- ballList) {
            destroyBall(ball)
          }
          ballList.clear()
        }

        println("finishgame")
      case 3 =>//perd
        val img = new BitmapImage("data/images/backgroundfin.jpg")
        g.drawBackground(img, 0, 0)
        time.elapsedTime = 30
        exitButton.draw(g)
        restart.draw(g)
        whereplaying = false
        playerList(0) = null
        if (players == 2)
          playerList(1) = null
        if (!ballList.isEmpty) {
          for (ball <- ballList) {
            destroyBall(ball)
          }
          ballList.clear()
        }

      case 4 =>
        for (ball <- ballList) {
          destroyBall(ball)
        }

        ballList.clear()
        playerList.clear()
        g.drawBackground(initialBackground, 0, 0)
        exitButton.draw(g)
        nextLevel.draw(g)
        whereplaying = false
        time.elapsedTime = 30
      case 0 => //lobby
        time.elapsedTime = 30
        g.clear()
        g.drawBackground(initialBackground, 0, 0)
        buttonsList.addOne(player1)
        buttonsList.addOne(player2)
        buttonsList.addOne(exitButton)
        whereplaying = false
        for (b <- buttonsList) {
          b.draw(g)
        }
        bonus = false
      case 1 => //playing
        buttonsList.clear()
        g.clear()
        g.drawFPS()
        g.drawSchoolLogo()

        if (whereplaying == false) {
          createPlayers()
          initialiseWalls()
          whereplaying = true
          if (players == 2) {
            playerList(0).ss = new Spritesheet("data/images/lumberjack_sheet.png", playerList(0).SPRITE_WIDTH, playerList(0).SPRITE_HEIGHT)
            playerList(0).draw(g)
            playerList(1).ss = new Spritesheet("data/images/lumberjack_sheet1.png", playerList(1).SPRITE_WIDTH, playerList(1).SPRITE_HEIGHT)
            playerList(1).draw(g)
          }
          else {
            playerList(0).ss = new Spritesheet("data/images/lumberjack_sheet.png", playerList(0).SPRITE_WIDTH, playerList(0).SPRITE_HEIGHT)
            playerList(0).draw(g)
          }
        }


        // draw players
        if (players == 2) {
          playerList(0).draw(g)
          playerList(1).draw(g)
        }
        else {
          playerList(0).draw(g)
        }


        // calculate time
        time.elapsedTime -= Gdx.graphics.getDeltaTime
        //draw time
        g.drawString(60, 1050, s"Time: ${time.elapsedTime.toInt}", Align.right)
        g.drawString(1890, 1050, s"Level: ${levelManager.level}/6", Align.right)


        //murs
        if (time.elapsedTime < 28) {
          leftwall.updateBox(+0.005f)
          rightwall.updateBox(-0.005f)
          rightwall.draw(g)
          leftwall.draw(g)
        }


        //check balls
        if (levelManager.level != 0 && levelPlaying == false) {
          levelPlaying = true
          if (levelManager.balls == 1) {
            val newBall = new Ball("Ball", levelManager.position1, levelManager.size)
            ballList += newBall
          }
          else if (levelManager.balls == 2) {
            levelPlaying = true
            ballList += new Ball("Ball1", levelManager.position1, levelManager.size)
            ballList += new Ball("Ball2", levelManager.position2, levelManager.size)
          }
        }

        //moves player
        if (rightKeyPressed1) {
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).POSX += 7
          }
        }
        else if (leftKeyPressed1) {
          if (playerList(0).POSX > 0) {
            playerList(0).POSX -= 7
          }
        }
        if (rightKeyPressed2) {
          if (playerList(1).POSX < getWindowWidth - playerList(1).SPRITE_WIDTH) {
            playerList(1).POSX += 7
          }
        }
        else if (leftKeyPressed2) {
          if (playerList(1).POSX > 0) {
            playerList(1).POSX -= 7
          }
        }

        for (b <- ballList) {
          b.draw(g)
          b.enableCollisionListener()
          if (b.checkCollisioWithPlayer(playerList(0)))
            start = true
          if (players == 2) {
            if (b.checkCollisioWithPlayer(playerList(1)))
              start = true
          }
          for (roap <- roapList) {
            if (roap != null) {
              if (b.checkCollisionWithBullet(roap)) {
                roapList(roapList.indexOf(roap)) = null
                b.destroy()
                if (b.radius == 16) {
                  if (ballList.length > 1)
                    bonus = true
                  getballx = b.position.x
                  getbally = b.position.y
                  ballsToRemove += b
                  ballsToRemove += b
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

                }
              }
              else if (roap.updateLine() == false)
                roapList(roapList.indexOf(roap)) = null
            }
          }
        }


        if (!ballsToRemove.isEmpty)
          ballList --= ballsToRemove

        if (!ballsToAdd.isEmpty)
          ballList ++= ballsToAdd

        for (bullet <- roapList) {
          if (bullet != null)
            bullet.draw(g)
        }


        if (ballList.isEmpty)
          levelPlaying = false


        //clear list to add and remove
        ballsToRemove.clear()
        ballsToAdd.clear()


        dbg.render(world, g.getCamera.combined)
        PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

        if (start || time.elapsedTime <= 0) {
          gameState = 3
        }
        else if (gameState == 1 && levelPlaying == false) {
          gameState = 4
        }

    }
    if (bonus && gameState == 1) {
      if (getbally >= 15)
        getbally = getbally - 2
      val b1 = new BonusTime(getballx, getbally)
      if (players == 1) {
        if (!b1.collision(playerList(0)))
          b1.draw(g)
        else {
          bonus = false
          time.addTime(time.elapsedTime)
        }
      } else if (players == 2) {
        if (!b1.collision(playerList(0)) || !b1.collision(playerList(0)))
          b1.draw(g)
        else {
          bonus = false
          time.addTime(time.elapsedTime)
        }
      }

    }
    if (levelManager.level == 6) {
      gameState = 2

    }
  }


  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    if (gameState == 1 && levelPlaying) {
      keycode match {
        case Input.Keys.UP =>
          if (roapList(0) == null) {
            roapList(0) = new Roap("Bullet", MyPoint2D(playerList(0).POSX + (playerList(0).SPRITE_WIDTH / 2), playerList(0).POSY))
            Logger.log("New bullet created")
          }
        case Input.Keys.RIGHT =>
          playerList(0).textureY = 2
          rightKeyPressed1 = false
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.LEFT =>
          playerList(0).textureY = 1
          leftKeyPressed1 = false
          if (playerList(0).POSX > 0) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.A =>
          if (players == 2) {
            playerList(1).textureY = 1
            leftKeyPressed2 = false
            if (playerList(1).POSX > 0) {
              playerList(1).playerBounds.setPosition(playerList(1).POSX, playerList(1).POSY)
            }
          }
        case Input.Keys.S =>
          if (players == 2) {
            if (roapList(1) == null) {
              roapList(1) = new Roap("Bullet", MyPoint2D(playerList(1).POSX + (playerList(1).SPRITE_WIDTH / 2), playerList(1).POSY))

            }

          }
        case Input.Keys.D =>
          if (players == 2) {
            playerList(1).textureY = 2
            rightKeyPressed2 = false
            if (playerList(1).POSX < getWindowWidth - playerList(1).SPRITE_WIDTH) {
              playerList(1).playerBounds.setPosition(playerList(1).POSX, playerList(1).POSY)
            }
          }

        case _ => playerList(0).textureY = 0
      }
    }
  }


  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)
    println(s"clicked on $x $y $button")
    if (player1.click(x, y) && gameState == 0) {
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
    else if (restart.click(x, y) && gameState == 3) {
      new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
      dbg = new DebugRenderer()
      start = !start
      if (leftwall != null && rightwall != null) {
        println("siuuu")
        removeWalls()
      }
      levelPlaying = false
      bonus = false
      gameState = 1
      roapList(0) = null
      if (players == 2)
        roapList(1) = null
      buttonsList.clear()
      createPlayers()

    }
    else if (restart.click(x, y) && gameState == 2) {
      new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
      dbg = new DebugRenderer()
      levelManager.levelRst()
      if (leftwall != null && rightwall != null) {
        println("siuuu")
        removeWalls()
      }

      start = false
      levelPlaying = false
      bonus = false
      gameState = 1
      roapList(0) = null
      if (players == 2)
        roapList(1) = null
      buttonsList.clear()
      //createPlayers()
      //levelManager.level=0
      //levelManager.balls=32
      gameState = 0
      println("here2")
    }
    else if (exitButton.click(x, y)) {
      System.exit(-1)
    }
    else if (nextLevel.click(x, y)) {
      if (leftwall != null && rightwall != null) {
        println("siuuu")
        removeWalls()
      }
      levelManager.levelUp()
      gameState = 1
      buttonsList.clear()
    }
  }


  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    if (gameState == 1 && levelPlaying) {
      keycode match {
        case Input.Keys.RIGHT =>
          playerList(0).textureY = 2
          rightKeyPressed1 = true
          if (playerList(0).POSX < getWindowWidth - playerList(0).SPRITE_WIDTH) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.LEFT =>
          playerList(0).textureY = 1
          leftKeyPressed1 = true
          if (playerList(0).POSX > 0) {
            playerList(0).playerBounds.setPosition(playerList(0).POSX, playerList(0).POSY)
          }
        case Input.Keys.A =>
          if (players == 2) {
            playerList(1).textureY = 1
            leftKeyPressed2 = true
            if (playerList(1).POSX > 0) {
              playerList(1).playerBounds.setPosition(playerList(1).POSX, playerList(1).POSY)
            }
          }
        case Input.Keys.S =>
          if (players == 2) {
            if (roapList(1) == null) {
              roapList(1) = new Roap("Bullet", MyPoint2D(playerList(1).POSX + (playerList(1).SPRITE_WIDTH / 2), playerList(1).POSY))

            }

          }
        case Input.Keys.D =>
          if (players == 2) {
            playerList(1).textureY = 2
            rightKeyPressed2 = true
            if (playerList(1).POSX < getWindowWidth - playerList(1).SPRITE_WIDTH) {
              playerList(1).playerBounds.setPosition(playerList(1).POSX, playerList(1).POSY)
            }
          }

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
