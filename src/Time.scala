import com.badlogic.gdx.Gdx

class Time {
var elapsedTime : Float =30


  def check(t : Float):Unit={
    var timevalue =t
    if (t > 20)
      timevalue= 30
    else
      timevalue +=10

    elapsedTime =timevalue
  }
}
