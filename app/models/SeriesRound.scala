package models

import models.matches.{SiteGame, SiteMatch, BracketMatch}
import models.player.{PlayerScores, Player, Rank, SeriesPlayer}
import play.api.libs.functional.syntax._
import utils.JsonUtils
import scala.language.postfixOps
import play.api.libs.json._
import play.api.libs.json.Reads._

sealed trait SeriesRound {
  def id: String
  def seriesId: String
  def roundNr: Int
}


case class SiteBracketRound(id: String, numberOfBracketRounds: Int, seriesId: String, roundNr: Int, bracketPlayers: List[SeriesPlayer], bracketRounds: List[List[BracketMatch]], roundType: String="B") extends SeriesRound {
  def getId(m: SiteBracketRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteBracketRound): SiteBracketRound = m.copy(id = id)

}

case class SiteRobinRound(id: String, numberOfRobinGroups: Int, seriesId: String, roundNr: Int, robinList: List[RobinGroup], roundType: String="R") extends SeriesRound {
  def getId(m: SiteRobinRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteRobinRound): SiteRobinRound = m.copy(id = id)

}

object SeriesRoundEvidence {

  implicit object seriesRoundIsModel extends Model[SeriesRound] {

    implicit val rankFormat = Json.format[Rank]
    implicit val playerScoresFormat = Json.format[PlayerScores]
    implicit val playerFormat = Json.format[Player]
    implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
    implicit val gameWrites = Json.format[SiteGame]
    implicit val matchWrites = Json.format[SiteMatch]
    implicit val bracketMatchWrites = Json.format[BracketMatch]
    implicit val listSeriesPlayerWrites = JsonUtils.listWrites(Json.writes[SeriesPlayer])
    implicit val listMatchReads = JsonUtils.listReads(Json.reads[SiteMatch])
    implicit val listSeriesPlayerReads = JsonUtils.listReads(Json.reads[SeriesPlayer])

    implicit val siteBracketRoundWrites: Writes[SiteBracketRound] = (
      (__ \ "id").write[String] and
        (__ \ "numberOfBracketRounds").write[Int] and
        (__ \ "seriesId").write[String] and
        (__ \ "roundNr").write[Int] and
        (__ \ "bracketPlayers").lazyWrite[List[SeriesPlayer]](Writes.list[SeriesPlayer](Json.writes[SeriesPlayer])) and
        (__ \ "bracketRounds").lazyWrite[List[List[BracketMatch]]](Writes.list[List[BracketMatch]](Writes.list[BracketMatch](bracketMatchWrites))) and
          (__ \ "roundType").write[String]
      ) (unlift(SiteBracketRound.unapply))

    implicit val siteBracketRoundReads: Reads[SiteBracketRound] = (
      (__ \ "id").read[String] and
        (__ \ "numberOfBracketRounds").read[Int] and
        (__ \ "seriesId").read[String] and
        (__ \ "roundNr").read[Int] and
        (__ \ "bracketPlayers").lazyRead[List[SeriesPlayer]](Reads.list[SeriesPlayer](Json.reads[SeriesPlayer])) and
        (__ \ "bracketRounds").lazyRead[List[List[BracketMatch]]](Reads.list[List[BracketMatch]](Reads.list[BracketMatch](Json.reads[BracketMatch])))
      ) (SiteBracketRound.apply(_, _, _, _, _, _))

    implicit val siteRobinRoundWrites: Writes[SiteRobinRound] = (
      (__ \ "id").write[String] and
        (__ \ "numberOfRobinGroups").write[Int] and
        (__ \ "seriesId").write[String] and
        (__ \ "roundNr").write[Int] and
        (__ \ "robinRounds").lazyWrite[List[RobinGroup]](Writes.list[RobinGroup](Json.writes[RobinGroup])) and
        (__ \ "roundType").write[String]
      ) (unlift(SiteRobinRound.unapply))

    implicit val siteRobinRoundReads: Reads[SiteRobinRound] = (
      (__ \ "id").read[String] and
        (__ \ "numberOfRobinGroups").read[Int] and
        (__ \ "seriesId").read[String] and
        (__ \ "roundNr").read[Int] and
        (__ \ "robinRounds").lazyRead[List[RobinGroup]](Reads.list[RobinGroup](Json.reads[RobinGroup]))
      ) (SiteRobinRound.apply(_, _, _, _, _))

    override def getId(m: SeriesRound): Option[String] = m match {
      case r: SiteRobinRound => r.getId(r)
      case b: SiteBracketRound => b.getId(b)
    }

    override def setId(id: String)(m: SeriesRound): SeriesRound = m match {
      case r: SiteRobinRound => r.setId(id)(r)
      case b: SiteBracketRound => b.setId(id)(b)
    }

    override def writes(o: SeriesRound): JsObject = o match {
      case r: SiteRobinRound => Json.toJson(r)(siteRobinRoundWrites).asInstanceOf[JsObject]
      case b: SiteBracketRound => Json.toJson(b)(siteBracketRoundWrites).asInstanceOf[JsObject]
    }

    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => Json.fromJson[SiteRobinRound](json)(siteRobinRoundReads)
      case "B" => Json.fromJson[SiteBracketRound](json)(siteBracketRoundReads)
    }


    implicit val roundReads = new Reads[SeriesRound] {
      override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
        case "R" => Json.fromJson(json)(siteRobinRoundReads)
        case "B" => Json.fromJson(json)(siteBracketRoundReads)
      }
    }
  }


}
