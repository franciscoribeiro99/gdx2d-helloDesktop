case class MyPoint2D(x: Float, y: Float)

case class Line(start: MyPoint2D, end: MyPoint2D)

class Bullet(name: String, var position: MyPoint2D) {
  var line = Line(position, MyPoint2D(position.x, position.y))

  def updateLine(): Boolean = {
    if (line.end.y < 1080) {
      line = Line(position, MyPoint2D(position.x, line.end.y + 8))
      return true
    } else {
      return false
    }

  }


}
