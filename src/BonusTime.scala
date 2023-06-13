import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics

class BonusTime(POSX :Int, POSY:Int) extends RenderingScreen{
  var img = new BitmapImage("data/images/clock.png")
  override def onInit(): Unit = {
    img = new BitmapImage("data/images/clock.png")
  }

  override def onGraphicRender(gdxGraphics: GdxGraphics): Unit = {
    gdxGraphics.drawPicture(POSX,POSY,img)
    println("r")
  }


}
