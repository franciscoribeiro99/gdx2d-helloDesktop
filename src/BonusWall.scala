import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.{Intersector, Rectangle}

class BonusWall(POSX:Float, POSY:Float) extends DrawableObject{
  var img = new BitmapImage("data/images/wallbonus.jpg")

  var imgBounds: Rectangle= null
  imgBounds = new Rectangle(POSX,POSY,60,60)

  override def draw(gdxGraphics: GdxGraphics): Unit = {
    gdxGraphics.drawPicture(POSX, POSY, img)

  }

  def collision(player: Player): Boolean = {
    Intersector.overlaps(imgBounds, player.playerBounds)
  }

}
