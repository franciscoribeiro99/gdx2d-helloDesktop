
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

  // ArrayBuffer of objects
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val bullets: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()
  //time
  var elapsedTime: Float = 30
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
  var player = new Player

  def initializeGameState(): Unit = {
    // Initialize game state components
    levelManager.levelRst()
    for (ball <- balls) {
      destroyBall(ball)
    }
    elapsedTime = 30
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
    dbg = new DebugRenderer()
    balls.clear()
    ballsToAdd.clear()
    ballsToRemove.clear()
    bullets.clear()
    bulletsToRemove.clear()
    levelManager.levelUp()
    println(levelManager.level)
    start = false
  }

  //TODO: Check why our game crashes when balls are being created

  override def onInit(): Unit = {
    setTitle("BubbleTrouble")
    initializeGameState()

    player.ss = new Spritesheet("data/images/lumberjack_sheet.png", player.SPRITE_WIDTH, player.SPRITE_HEIGHT)

  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    g.drawFPS()
    g.drawSchoolLogo()
    player.draw(g)

    if (rightKeyPressed) {
      if (player.POSX < getWindowWidth - player.SPRITE_WIDTH) {
        player.POSX += 5
      }
    }

    if (leftKeyPressed) {
      if (player.POSX > 0) {
        player.POSX -= 5
      }
    }
    elapsedTime -= Gdx.graphics.getDeltaTime
    g.drawString(60, 1050, s"Time: ${elapsedTime.toInt}", Align.right)
    if (levelManager.level != 0 && levelPlaying == false) {
      if (levelManager.balls == 1) {
        levelPlaying = true
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
      if (b.checkCollisioWithPlayer(player)) {
        start = true
      }
      for (bullet <- bullets) {
        if (b.checkCollisionWithBullet(bullet)) {
          if (b.radius == 16) {
            b.destroy()
            ballsToRemove += b
            bulletsToRemove += bullet
          }
          else {
            b.destroy()
            if (elapsedTime > 20)
              elapsedTime = 30
            else
              elapsedTime = elapsedTime + 10

            val ball1 = new Ball("Ball", new Vector2(b.ballBounds.x + 10, b.ballBounds.y), b.radius / 2)
            val ball2 = new Ball("Ball", new Vector2(b.ballBounds.x - 10, b.ballBounds.y), b.radius / 2)

            // Calcul late velocities for the new balls
            val angle1 = b.getBodyAngle + 60
            val angle2 = b.getBodyAngle + 60

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

    if (start || elapsedTime <= 0) {
      val img = new BitmapImage("data/images/backgroundfin.jpg")
      g.drawBackground(img, 10f, 10f)
    }
    else if (!start && !levelPlaying)
      levelManager.levelUp()
  }

  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)

    if (button == 0 && !start) {

    }
  }

  private var keyPressed: Set[Int] = Set()

  // Verifica se un tasto è stato premuto solo una volta
  private def onlyKeyPressed(keycode: Int): Boolean = {
    if (!keyPressed.contains(keycode)) {
      keyPressed += keycode
      true
    } else {
      false
    }
  }

  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)

    keycode match {
      case Input.Keys.SPACE =>
        if (bullets.isEmpty) {
          val newBullet = new Bullet("Bullet", MyPoint2D(player.POSX + (player.SPRITE_WIDTH / 2), player.POSY))
          bullets += newBullet
          Logger.log("New bullet created")
        }
      case Input.Keys.RIGHT =>
        player.textureY = 2
        rightKeyPressed = false
        if (player.POSX < getWindowWidth - player.SPRITE_WIDTH) {
          player.POSX += 20
          player.playerBounds.setPosition(player.POSX, player.POSY)
        }
      case Input.Keys.LEFT =>
        player.textureY = 1
        leftKeyPressed = false
        if (player.POSX > 0) {
          player.POSX -= 20
          player.playerBounds.setPosition(player.POSX, player.POSY)
        }
      case Input.Keys.ENTER =>
        resetGame()
      case _ => player.textureY = 0
    }
  }


  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)

    keycode match {
      case Input.Keys.SPACE =>
        if (bullets.isEmpty) {
          val newBullet = new Bullet("Bullet", MyPoint2D(player.POSX + (player.SPRITE_WIDTH / 2), player.POSY))
          bullets += newBullet
          Logger.log("New bullet created")
        }
      case Input.Keys.RIGHT =>
        player.textureY = 2
        rightKeyPressed = true
        if (player.POSX < getWindowWidth - player.SPRITE_WIDTH) {
          player.POSX += 20
          player.playerBounds.setPosition(player.POSX, player.POSY)
        }
      case Input.Keys.LEFT =>
        player.textureY = 1
        leftKeyPressed = true
        if (player.POSX > 0) {
          player.POSX -= 20
          player.playerBounds.setPosition(player.POSX, player.POSY)
        }
      case Input.Keys.ENTER =>
        resetGame()
      case _ => player.textureY = 0
    }

  }


  def resetGame(): Unit = {
    // Reset the game state
    start = false
    player.POSX = getWindowWidth / 2 - player.SPRITE_WIDTH / 2
    initializeGameState()
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
