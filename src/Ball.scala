
import ch.hevs.gdx2d.components.physics.primitives.PhysicsCircle
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class Ball(name: String, var position: Vector2, var radius: Int) extends PhysicsCircle(name, position, radius) with DrawableObject {
  override def draw(gdxGraphics: GdxGraphics): Unit = {
    var position = getBodyPosition
    var radius: Float = getBodyRadius
    var color: Color= Color.WHITE
    radius match {
      case _ =>color=Color.RED
    }
    gdxGraphics.drawFilledCircle(position.x,position.y,radius,color)
  }
}
