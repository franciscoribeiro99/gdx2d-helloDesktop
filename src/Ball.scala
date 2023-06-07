import ch.hevs.gdx2d.components.physics.primitives.PhysicsCircle
import ch.hevs.gdx2d.lib.GdxGraphics
import ch.hevs.gdx2d.lib.interfaces.DrawableObject
import ch.hevs.gdx2d.lib.physics.AbstractPhysicsObject
import ch.hevs.gdx2d.lib.utils.Logger
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Circle, Intersector, Vector2}

class Ball(name: String, var position: Vector2, var radius: Int) extends PhysicsCircle(name, position, radius) with DrawableObject {
  var ballBounds: Circle = null
  ballBounds = new Circle(position, radius)
  println("new ball")
  var shouldBeDestroyed : Boolean = false

  override def draw(gdxGraphics: GdxGraphics): Unit = {
    var position = getBodyPosition
    var radius: Float = getBodyRadius
    ballBounds = new Circle(position, radius)
    var color: Color = Color.WHITE
    radius match {
      case _ => color = Color.RED
    }
  //  gdxGraphics.drawFilledCircle(position.x, position.y, radius, color)
  }

  override def collision(theOtherObject: AbstractPhysicsObject, energy: Float): Unit = {
    Logger.log(name + " collided " + theOtherObject.name + " with energy " + energy )
    if (theOtherObject.name == "ground") {
      setBodyLinearVelocity(getBodyLinearVelocity.x, -(getBodyLinearVelocity.y))
    }
    else
      Logger.log(name + " collided with " + theOtherObject.name)
  }

  def checkCollisionWithBullet(bullet: Bullet): Boolean = {
    println("collision with bullet")
    shouldBeDestroyed = Intersector.overlaps(ballBounds, bullet.bulletBounds)
    shouldBeDestroyed
  }


  def checkCollisioWithPlayer(player: Player): Boolean = {
    //println("collision with player")
    return Intersector.overlaps(ballBounds, player.playerBounds)
  }


}
