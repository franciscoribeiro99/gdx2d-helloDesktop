import ch.hevs.gdx2d.components.bitmaps.Spritesheet
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle

class Player extends DrawableObject {
  val SPRITE_WIDTH = 128
  val SPRITE_HEIGHT = 128
  val FRAME_TIME = 0.58
  var dt: Float = 0
  var currentFrame = 0
  val nFrames = 4
  var textureY = 1
  var POSX = 1920 / 2 - SPRITE_WIDTH / 2
  var POSY = 0
  var playerBounds: Rectangle = new Rectangle(POSX, POSY, SPRITE_WIDTH, SPRITE_HEIGHT)
  var ss: Spritesheet = null

  override def draw(gdxGraphics: GdxGraphics): Unit = {
    playerBounds = new Rectangle(POSX, POSY, SPRITE_WIDTH, SPRITE_HEIGHT)
    gdxGraphics.draw(ss.sprites(textureY)(currentFrame), POSX, POSY)
    dt += Gdx.graphics.getDeltaTime
    if (dt > FRAME_TIME) {
      dt = 0
      currentFrame = (currentFrame + 1) % nFrames
    }


  }

}
