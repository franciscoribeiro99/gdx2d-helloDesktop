import ch.hevs.gdx2d.components.bitmaps.{BitmapImage, Spritesheet}
import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.{Gdx, Input}

import scala.collection.mutable.ArrayBuffer

class Graphics extends PortableApplication(1920, 1080) {
  //ArrayBuffer of objects
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val bullets: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()

  //physics
  val world: World = PhysicsWorld.getInstance()
  var dbg: DebugRenderer = null
  var start = false
  var player = new Player

  def initializeGameState(): Unit = {
    // Initialize game state components
    dbg = new DebugRenderer()
    world.setGravity(new Vector2(0, -1.2f))
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
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

    val ballsToAdd: ArrayBuffer[Ball] = ArrayBuffer()
    val ballsToRemove: ArrayBuffer[Ball] = ArrayBuffer()

    // Create an ArrayBuffer to store bullets that need to be removed
    val bulletsToRemove: ArrayBuffer[Bullet] = ArrayBuffer()

    for (b <- balls) {
      b.draw(g)
      b.enableCollisionListener()
      if (b.checkCollisioWithPlayer(player)) {
        start = false
      }
      for (bullet <- bullets) {
        if (b.checkCollisionWithBullet(bullet)) {
          var ball1=new Ball("Ball", new Vector2(b.ballBounds.x, b.ballBounds.y), b.radius / 2)
          ball1.setBodyLinearVelocity(b.getBodyLinearVelocity.x,-b.getBodyLinearVelocity.y)
          var ball2= new Ball("Ball", new Vector2(b.ballBounds.x, b.ballBounds.y), b.radius / 2)
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
      if (bullet.updateLine() == false)
        bulletsToRemove += bullet
      else
        bullet.draw(g)
    }

    // Remove the bullets that need to be removed
    bullets --= bulletsToRemove

    dbg.render(world, g.getCamera.view)
    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)

    if (start == true) {
      var img = new BitmapImage("data/images/backgroundfin.jpg")
      g.drawBackground(img, 10f, 10f)
    }
  }


  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)
    if (button == 0 && start == false) {
      val newBall = new Ball("a ball", new Vector2(x, y), 50)
      balls += newBall
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)

    keycode match {

      case Input.Keys.SPACE =>
        if (bullets.length == 0) {
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
    // Reimposta lo stato del gioco
    balls.clear()
    bullets.clear()
    player.POSX = this.getWindowWidth / 2 - player.SPRITE_WIDTH / 2
    player.POSY = 50
    initializeGameState()
    start = false
  }

}

object test {
  def main(args: Array[String]): Unit = {
    new Graphics
  }
}
