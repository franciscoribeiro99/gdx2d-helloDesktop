import ch.hevs.gdx2d.components.physics.primitives.PhysicsStaticBox
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class Wall(name: String, position: Vector2, var width: Float, height: Float) extends PhysicsStaticBox(name, position, 1, 1080) with DrawableObject {
  var widthChange = 0.0f

  def updateBox(howMuch: Float): Unit = {
    //println(s"position en x ${getBody.getPosition.x}et en y ${getBody.getPosition.y}")
    getBody.setTransform(new Vector2(getBody.getPosition.x + howMuch, getBody.getPosition.y), 0.0f)
    widthChange += math.abs(howMuch * 150)
  }


  override def draw(gdxGraphics: GdxGraphics): Unit = {
    if (name == "wallLeft") {
      gdxGraphics.drawFilledRectangle(0, (height / 2)-60, 150 * getBody.getPosition.x, 1080, 0.0f, Color.RED)
    } else if (name == "wallright") {
      var position = getBody.getPosition.x * 150
      var right = 3840 - position
      println(s"position is $position et right vaut $right")
      gdxGraphics.drawFilledRectangle(1920, (height / 2)-60, right , 1080, 0.0f, Color.RED)

    }
  }


}

