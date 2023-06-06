import ch.hevs.gdx2d.components.physics.primitives.PhysicsCircle
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Rectangle, Vector2}

class Ball(name: String, var position: Vector2, var radius: Int) extends PhysicsCircle(name, position, radius) with DrawableObject {
  var lastCollision: Float = 0.5f
  var ballSplit = false
  var ball1: Ball = _
  var ball2: Ball = _
  var ballBounds: Rectangle = null
  ballBounds = new Rectangle(
    position.x - radius,
    position.y - radius,
    radius * 2,
    radius * 2
  )


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


  }

  def checkCollisionWithBullet(bullet: Bullet): Boolean = {
    var bulletBounds: Rectangle = new Rectangle(bullet.line.start.x, bullet.line.start.y, bullet.line.end.x, bullet.line.end.y)
    return ballBounds.overlaps(bulletBounds)
  }


  def checkCollisioWithPlayer(player: Player): Boolean = {
    return ballBounds.overlaps(player.playerBounds)
  }

}
