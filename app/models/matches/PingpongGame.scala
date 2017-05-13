package models.matches

case class PingpongGame(pointA: Int, pointB : Int, gameNr: Int) {

  def isCorrect(targetScore: Int): Boolean ={
    if(pointA > targetScore && pointA> pointB){
      pointA - pointB == 2
    }else if(pointB > targetScore && pointB > pointA){
      pointB - pointA == 2
    } else if(pointA==targetScore){
      pointA - pointB > 1
    }else if (pointB == targetScore){
      pointB - pointA  > 1
    }else{
      false
    }
  }
}
