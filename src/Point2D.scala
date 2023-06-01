class MyPoint2D(var x: Int, var y: Int) {


  def lineCol(Linestart: MyPoint2D, LineEnd: MyPoint2D, pointToCheck: MyPoint2D): Boolean = {
    var pente = (LineEnd.y - Linestart.y) / (LineEnd.x - Linestart.x)
    var ordOrigine = Linestart.y - (pente * Linestart.x)

    if(ordOrigine == (pointToCheck.y - (pente* pointToCheck.x)))
      return true
    else
      return false
  }


}
