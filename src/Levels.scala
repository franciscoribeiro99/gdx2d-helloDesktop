import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.Vector2

class Levels extends DrawableObject{
  var level = 0
  var balls = 0
  var size = 0

  var position1 = new Vector2(300, 800)
  var position2 = new Vector2(1620, 800)

  override def draw(gdxGraphics: GdxGraphics): Unit = ???


  def levelUp(): Unit = {
    level += 1
    level match {
      case 0 =>
      case 1 =>
        balls = 1
        size = 32
      case 2 =>
        balls = 1
        size = 64
      case 3 =>
        balls = 1
        size = 128
      case 4 =>
        balls = 2
        size = 32
      case 5 =>
        balls = 2
        size = 64
      case 6 =>
        balls = 2
        size = 128
      case _ =>
        size = 0
        balls = 0
    }
  }

  def levelRst(): Unit = {
    level = 0
    balls = 0
    size = 0
  }


}
