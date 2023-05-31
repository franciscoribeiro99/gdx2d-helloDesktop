import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.desktop.physics.DebugRenderer
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.physics.PhysicsWorld
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World

import scala.collection.mutable.ArrayBuffer

class Graphics extends PortableApplication(1080,920) {
  val balls: ArrayBuffer[Ball] = ArrayBuffer[Ball]()

  val world : World = PhysicsWorld.getInstance()
  var dbg : DebugRenderer = null

  override def onInit(): Unit = {
    setTitle("BubbleTrouble")
    dbg = new DebugRenderer()
    import ch.hevs.gdx2d.components.physics.utils.PhysicsScreenBoundaries
    new PhysicsScreenBoundaries(getWindowWidth, getWindowHeight)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    g.drawStringCentered(getWindowHeight * 0.8f, "Welcome to gdx2d !")
    g.drawFPS()
    g.drawSchoolLogo()

    for(b <- balls)
      b.draw(g)

    import ch.hevs.gdx2d.lib.physics.PhysicsWorld
    import com.badlogic.gdx.Gdx

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

}
object test {
  def main(args: Array[String]): Unit = {
    new Graphics
  }
}
