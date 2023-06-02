import ch.hevs.gdx2d.components.bitmaps.Spritesheet
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
  //ArrayBuffer of objecets
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()
  val bullets: ArrayBuffer[Bullet] = ArrayBuffer[Bullet]()

  //physics
  val world: World = PhysicsWorld.getInstance()
  var dbg: DebugRenderer = null


  val SPRITE_WIDTH = 128
  val SPRITE_HEIGHT = 128
  val FRAME_TIME = 0.55
  var dt: Float = 0
  var currentFrame = 0
  val nFrames = 4
  var textureY = 1
  var ss: Spritesheet = null
  var POSX = this.getWindowWidth / 2 - SPRITE_WIDTH / 2
  var POSY = 50

  override def onInit(): Unit = {
    setTitle("BubbleTrouble")
    ss = new Spritesheet("data/images/lumberjack_sheet.png", SPRITE_WIDTH, SPRITE_HEIGHT)
    dbg = new DebugRenderer()
    world.setGravity(new Vector2(0, -1.2f))
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
  }


  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()
    ss = new Spritesheet("data/images/lumberjack_sheet.png", SPRITE_WIDTH, SPRITE_HEIGHT)
    g.drawFPS()
    g.drawSchoolLogo()


    for (b <- balls) {
      b.draw(g)
      b.enableCollisionListener()
      if (b.ballSplit == true) {
        balls += b.ball1
        balls += b.ball2
        PhysicsWorld.getInstance().destroyBody(b.getBody)
      }
    }

    // Create an ArrayBuffer to store bullets that need to be removed
    val bulletsToRemove: ArrayBuffer[Bullet] = ArrayBuffer()

    for (bullet <- bullets) {

      if (bullet.updateLine()) {
        // Draw the bullet
        g.drawLine(bullet.line.start.x, bullet.line.start.y, bullet.line.end.x, bullet.line.end.y)
        //draw the pointer
        g.drawLine(bullet.line.end.x, bullet.line.end.y, bullet.line.end.x - 10, bullet.line.end.y - 10)
        g.drawLine(bullet.line.end.x, bullet.line.end.y, bullet.line.end.x + 10, bullet.line.end.y - 10)
      }
      else {
        // Add the bullet to the removal list
        bulletsToRemove += bullet
      }
    }
    // Remove the bullets that need to be removed
    bullets --= bulletsToRemove


    dt += Gdx.graphics.getDeltaTime()
    if (dt > FRAME_TIME) {
      dt = 0
      currentFrame = (currentFrame + 1) % nFrames
    }
    g.draw(ss.sprites(textureY)(currentFrame), POSX, POSY)
    dbg.render(world, g.getCamera.view)

    PhysicsWorld.updatePhysics(Gdx.graphics.getDeltaTime)
  }


  override def onClick(x: Int, y: Int, button: Int): Unit = {
    super.onClick(x, y, button)

    if (button == 0) {
      val newBall = new Ball("a ball", new Vector2(x, y), 50)
      balls += newBall
    }
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)

    keycode match {

      case Input.Keys.SPACE =>
        if (bullets.length == 0) {
          val newBullet = new Bullet("Bullet", MyPoint2D(POSX + (SPRITE_WIDTH / 2), POSY))
          bullets += newBullet
          Logger.log("New bullet created")
        }
      case Input.Keys.DPAD_RIGHT => textureY = 2
        if (POSX < this.getWindowWidth) {
          POSX += 20

        }
      case Input.Keys.DPAD_LEFT => textureY = 1
        if (POSX < this.getWindowWidth) {
          POSX -= 20
        }
      case _ => textureY = 0
    }
  }

}

object test {
  def main(args: Array[String]): Unit = {
    new Graphics
  }
}
