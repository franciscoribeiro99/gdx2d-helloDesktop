import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.{Circle, Intersector, Vector2}

class BonusTime(POSX :Float, POSY:Float) extends DrawableObject{
  var img = new BitmapImage("data/images/clock.png")

  var imgBounds: Circle = null
  imgBounds = new Circle(new Vector2(POSX.toInt,POSY.toInt), 35)
  override def draw(gdxGraphics: GdxGraphics): Unit = {
    gdxGraphics.drawPicture(POSX, POSY, img)

  }
  def collision(player: Player):Boolean={
    Intersector.overlaps(imgBounds, player.playerBounds)
  }

}
