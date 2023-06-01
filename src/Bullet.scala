

case class Line(start: MyPoint2D, end: MyPoint2D)

class Bullet(name: String, var position: MyPoint2D, var radius: Int, height: Int) {
  var line = new Line(position, new MyPoint2D(position.x, 0))

  def updateLine(): Unit = {
    line = new Line(position, new MyPoint2D(position.x, position.y + 1))
  }


}
//Intersector
