import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.Vector2

class Wall(name: String, position: Vector2, width: Float, height: Float) extends PhysicsStaticBox(name, position,width,height) with DrawableObject {


  override def draw(gdxGraphics: GdxGraphics): Unit = {

  }


}
