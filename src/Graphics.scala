import ch.hevs.gdx2d.components.bitmaps.{BitmapImage, Spritesheet}
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable.ArrayBuffer

class Graphics extends PortableApplication(1920, 1080) {
  // ArrayBuffer of objects
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val bullets: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()

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

    for (ball<- balls){
      destroyBall(ball)
    }
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
    dbg = new DebugRenderer()
    balls.clear()
    ballsToAdd.clear()
    ballsToRemove.clear()
    bullets.clear()
    bulletsToRemove.clear()
    start = false
  }

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

    for (b <- balls) {
      b.draw(g)
      b.enableCollisionListener()
      if (b.checkCollisioWithPlayer(player)) {
        start = true
      }
      for (bullet <- bullets) {
        if (b.checkCollisionWithBullet(bullet)) {
          val ball1 = new Ball("Ball", new Vector2(b.ballBounds.x, b.ballBounds.y), b.radius / 2)
          val ball2 = new Ball("Ball", new Vector2(b.ballBounds.x, b.ballBounds.y), b.radius / 2)

          // Calculate velocities for the new balls
          val angle1 = b.getBodyAngle + 60
          val angle2 = b.getBodyAngle - 60

          val velocity1 = new Vector2(MathUtils.cosDeg(angle1), MathUtils.sinDeg(angle1))
          val velocity2 = new Vector2(MathUtils.cosDeg(angle2), MathUtils.sinDeg(angle2))

          ball1.setBodyLinearVelocity(velocity1)
          ball2.setBodyLinearVelocity(velocity2)

          b.destroy()
          ballsToAdd += ball1
          ballsToAdd += ball2
          ballsToRemove += b
          bulletsToRemove += bullet
        }
      }
    }

    balls --= ballsToRemove
    balls ++= ballsToAdd

    for (bullet <- bullets) {
      if (!bullet.updateLine()) {
        bulletsToRemove += bullet
      } else {
        bullet.draw(g)
      }
    }

    // Remove the bullets that need to be removed
    bullets --= bulletsToRemove

    //clear list to add and remove
    ballsToRemove.clear()
    ballsToAdd.clear()
    bulletsToRemove.clear()


    dbg.render(world, g.getCamera.view)
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    if (start) {
      val img = new BitmapImage("data/images/backgroundfin.jpg")
      g.drawBackground(img, 10f, 10f)
    }
  }

  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)

    if (button == 0 && !start) {
      val newBall = new Ball(s"Ball ${System.currentTimeMillis()}", new Vector2(x, y), 50)
      balls += newBall
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
        if (player.POSX < getWindowWidth - player.SPRITE_WIDTH) {
          player.POSX += 20
          player.playerBounds.setPosition(player.POSX, player.POSY)
        }
      case Input.Keys.LEFT =>
        player.textureY = 1
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
