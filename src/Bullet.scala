

case class Line(start: MyPoint2D, end: MyPoint2D)

class Bullet(name: String, var position: MyPoint2D, var radius: Int, height: Int) {
  val line = new Line(position, new MyPoint2D(position.x, position.y))

  def updateLine(): Unit = {


  }


}
//Intersector
