import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.graphics.g3d.particles.influencers.ModelInfluencer.Random
import com.badlogic.gdx.math.Vector2
import jdk.jfr.consumer.RecordedFrame

class Levels extends DrawableObject{
  var level = 0
  var balls = 0
  var size = 0

  val randx1: scala.util.Random = scala.util.Random
  val randy1: scala.util.Random = scala.util.Random
  val randx2: scala.util.Random = scala.util.Random
  val randy2: scala.util.Random = scala.util.Random
   var position1 = new Vector2(randx1.between(200,1700), randy1.between(700,1000))
   var  position2 = new Vector2(randx2.between(1000,1700),randy2.between(700,1000))


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
