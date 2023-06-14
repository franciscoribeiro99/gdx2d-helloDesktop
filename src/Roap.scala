import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.Rectangle

case class MyPoint2D(x: Float, y: Float)

case class Line(start: MyPoint2D, end: MyPoint2D)

class Roap(name: String, var position: MyPoint2D)extends DrawableObject{
  var line = Line(position, MyPoint2D(position.x, position.y))
  var bulletBounds: Rectangle = new Rectangle(line.start.x, line.start.y, line.end.x, line.end.y)
  def updateLine(): Boolean = {
    if (line.end.y < 1080) {
      line = Line(position, MyPoint2D(position.x, line.end.y + 3))
      bulletBounds= new Rectangle(line.start.x, line.start.y, 3, line.end.y)
       true
    } else {
      false
    }
  }

  override def draw(gdxGraphics: GdxGraphics): Unit = {
    if (updateLine()) {
      // Draw the bullet
      gdxGraphics.drawLine(line.start.x, line.start.y, line.end.x, line.end.y)
      //draw the pointer
      gdxGraphics.drawLine(line.end.x, line.end.y, line.end.x - 10, line.end.y - 10)
      gdxGraphics.drawLine(line.end.x, line.end.y, line.end.x + 10, line.end.y - 10)
    }

  }

}
