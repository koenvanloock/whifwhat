package models

/**
  * @author Koen Van Loock
  * @version 1.0 24/04/2016 0:19
  */
case class SiteGame(id: Option[String], matchId: String, pointA: Int, pointB : Int) extends Crudable[SiteGame]{
  override def getId: String = id.get

  override def setId(newId: String): SiteGame = this.copy(id=Some(newId))

  def isCorrect(targetScore: Int): Boolean ={
    if(pointA==targetScore){
      pointA - pointB > 1
    }else if (pointB == targetScore){
      pointB - pointA  > 1
    }else{
      if(pointA > targetScore && pointA> pointB){
        pointA - pointB == 2
      }else{
        pointB - pointA == 2
      }
    }

  }
}