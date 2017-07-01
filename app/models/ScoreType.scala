package models

case class ScoreType(name: String)

object ScoreTypes {
  def parseScoreType(scoreType: String) = scoreType match {
    case "confrontation" => DIRECT_CONFRONTATION
    case _ => PURE_POINT_BASED
  }

  val DIRECT_CONFRONTATION = ScoreType("Direct confrontation")
  val PURE_POINT_BASED = ScoreType("Point based")
}
