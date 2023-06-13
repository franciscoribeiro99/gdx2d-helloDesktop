import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.math.{Rectangle, Vector2}

class ClickableButton(var buttonName: String, image: BitmapImage,  positionX: Int,  positionY: Int) extends DrawableObject {

  val buttonBounds: Rectangle = new Rectangle(positionX, positionY, image.getImage.getWidth, image.getImage.getHeight)

  override def draw(g: GdxGraphics): Unit = {
    g.drawPicture(positionX, positionY, image)
  }

  def click(x: Int, y: Int): Boolean = {
    if (buttonBounds.contains(new Vector2(x, y))) {
      println(s"touche $buttonName")
      return true
    }
    false
  }

}
