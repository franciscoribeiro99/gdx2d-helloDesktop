import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Rectangle, Vector2}

class Wall(name: String, position: Vector2, var width: Float, height: Float) extends PhysicsStaticBox(name, position, 1, 1080) with DrawableObject {

  var boundaryBox = new Rectangle(position.x, position.y, width, height)

  def updateBox(howMuch: Float): Unit = {
    println(s"position en x ${getBody.getPosition.x}et en y ${getBody.getPosition.y}")
    getBody.setTransform(new Vector2(getBody.getPosition.x + howMuch, getBody.getPosition.y), 0.0f)
    if (howMuch > 0.0f)
      boundaryBox.setWidth(boundaryBox.getWidth + howMuch)

  }


  override def draw(gdxGraphics: GdxGraphics): Unit = {
    gdxGraphics.drawFilledRectangle(position.x/2, boundaryBox.y.toFloat, boundaryBox.width.toFloat, boundaryBox.height.toFloat, 0.0f, Color.RED)

  }


}
