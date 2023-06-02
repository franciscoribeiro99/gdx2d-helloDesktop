import ch.hevs.gdx2d.components.physics.primitives.PhysicsCircle
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class Ball(name: String, var position: Vector2, var radius: Int) extends PhysicsCircle(name, position, radius) with DrawableObject {

  var ballSplit = false
  var ball1: Ball = _
  var ball2: Ball = _


  override def draw(gdxGraphics: GdxGraphics): Unit = {
    var position = getBodyPosition
    var radius: Float = getBodyRadius
    var color: Color = Color.WHITE
    radius match {
      case _ => color = Color.RED
    }
    gdxGraphics.drawFilledCircle(position.x, position.y, radius, color)
  }

  override def collision(theOtherObject: AbstractPhysicsObject, energy: Float): Unit = {
    Logger.log(name + " collided " + theOtherObject.name + " with energy " + energy)
    if (theOtherObject.name == "ground") {
      setBodyLinearVelocity(getBodyLinearVelocity.x, -(getBodyLinearVelocity.y))
    }
    if (theOtherObject.name == "Bullet") {
      val newRadius = getBodyRadius / 2
      if (newRadius > 10) {
        val BallPos = getBodyPosition
        val BallVelo = getBodyLinearVelocity
        ball1 = new Ball("SplitBall1", position, newRadius.toInt)
        ball2 = new Ball("SplitBall1", position, newRadius.toInt)
        ball1.setBodyLinearVelocity(BallVelo.x - 1, BallVelo.y)
        ball2.setBodyLinearVelocity(BallVelo.x + 1, BallVelo.y)
        ballSplit = true

      }
    }
  }
}
