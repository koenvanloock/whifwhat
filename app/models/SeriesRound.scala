package models

import models.matches.{MatchChecker, PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Rank, SeriesPlayer}
import models.types._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.JsonUtils

import scala.language.postfixOps

sealed trait SeriesRound {
  def matches: List[PingpongMatch]

  def isComplete: Boolean

  def id: String

  def seriesId: String

  def roundNr: Int

  def roundPlayers: List[SeriesPlayer]

  def scoreKey: Option[List[Int]]
}


case class SiteBracketRound(id: String, numberOfBracketRounds: Int, seriesId: String, roundNr: Int, bracketPlayers: List[SeriesPlayer], bracket: Bracket[PingpongMatch], scoreKey: Option[List[Int]]= None, roundType: String="B") extends SeriesRound {
  def getId(m: SiteBracketRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteBracketRound): SiteBracketRound = m.copy(id = id)

  def isComplete = SiteBracket.isComplete(this.bracket)

  def roundPlayers =  this.bracketPlayers

  def matches = this.bracket.toList

}


case class SiteRobinRound(id: String, numberOfRobinGroups: Int, seriesId: String, roundNr: Int, robinList: List[RobinGroup], scoreKey: Option[List[Int]] = None, roundType: String = "R") extends SeriesRound {
  def getId(m: SiteRobinRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteRobinRound): SiteRobinRound = m.copy(id = id)

  def isComplete = this.robinList.forall( robingroup => robingroup.robinMatches.forall(MatchChecker.isWon))

  def roundPlayers = robinList.flatMap(_.robinPlayers)

  def matches = robinList.flatMap(robinGroup => robinGroup.robinMatches)
}


object SeriesRoundEvidence {

  implicit object seriesRoundIsModel extends Model[SeriesRound] {

    implicit val rankFormat = Json.format[Rank]
    implicit val playerScoresFormat = Json.format[PlayerScores]
    implicit val playerFormat = Json.format[Player]
    implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
    implicit val gameWrites = Json.format[PingpongGame]
    implicit val matchWrites = Json.format[PingpongMatch]
    implicit val listSeriesPlayerWrites = JsonUtils.listWrites(Json.writes[SeriesPlayer])
    implicit val listMatchReads = JsonUtils.listReads(Json.reads[PingpongMatch])
    implicit val listSeriesPlayerReads = JsonUtils.listReads(Json.reads[SeriesPlayer])


    implicit val siteBracketRoundWrites: Writes[SiteBracketRound] = (
      (__ \ "id").write[String] and
        (__ \ "numberOfBracketRounds").write[Int] and
        (__ \ "seriesId").write[String] and
        (__ \ "roundNr").write[Int] and
        (__ \ "bracketPlayers").lazyWrite[List[SeriesPlayer]](Writes.list[SeriesPlayer](Json.writes[SeriesPlayer])) and
        (__ \ "bracketRounds").lazyWrite[Bracket[PingpongMatch]](bracketSiteMatchWrites) and
          (__ \ "scoreKey").writeNullable[List[Int]] and
          (__ \ "roundType").write[String]
      ) (unlift(SiteBracketRound.unapply))


    implicit val siteBracketRoundReads: Reads[SiteBracketRound] = (
      (__ \ "id").read[String] and
        (__ \ "numberOfBracketRounds").read[Int] and
        (__ \ "seriesId").read[String] and
        (__ \ "roundNr").read[Int] and
        (__ \ "bracketPlayers").lazyRead[List[SeriesPlayer]](Reads.list[SeriesPlayer](Json.reads[SeriesPlayer])) and
        (__ \ "bracketRounds").lazyRead[Bracket[PingpongMatch]](bracketSiteMatchReads) and
          (__ \ "scoreKey").readNullable[List[Int]]
      ) (SiteBracketRound.apply(_, _, _, _, _, _, _))


    implicit val leafReads: Reads[BracketLeaf[PingpongMatch]] = new Reads[BracketLeaf[PingpongMatch]] {
      override def reads(json: JsValue): JsResult[BracketLeaf[PingpongMatch]] = {
        (json \ "value").validate[PingpongMatch].map(x => BracketLeaf(x))
      }
    }

    implicit val leafWrites: Writes[BracketLeaf[PingpongMatch]] = new Writes[BracketLeaf[PingpongMatch]] {
      override def writes(leafMatch: BracketLeaf[PingpongMatch]): JsValue = {
        Json.obj("value" -> leafMatch.value)
      }
    }

    implicit val nodeReads: Reads[BracketNode[PingpongMatch]] = (
      (__ \ "value").read[PingpongMatch] and
        (__ \ "left").lazyRead[Bracket[PingpongMatch]](bracketSiteMatchReads) and
        (__ \ "right").lazyRead[Bracket[PingpongMatch]](bracketSiteMatchReads)
      ) (BracketNode[PingpongMatch](_, _, _))

    implicit val nodeWrites: Writes[SiteMatchNode] = (
      (__ \ "value").write[PingpongMatch] and
        (__ \ "left").lazyWrite[Bracket[PingpongMatch]](bracketSiteMatchWrites) and
        (__ \ "right").lazyWrite[Bracket[PingpongMatch]](bracketSiteMatchWrites)
      )(unlift(SiteMatchNode.unapply))

    implicit def bracketSiteMatchReads = new Reads[Bracket[PingpongMatch]] {
      override def reads(json: JsValue): JsResult[Bracket[PingpongMatch]] = (json \ "left").asOpt[JsValue] match {
        case Some(node) => Json.fromJson[BracketNode[PingpongMatch]](json)(nodeReads)
        case None => Json.fromJson[BracketLeaf[PingpongMatch]](json)(leafReads)
      }
    }

    implicit def bracketSiteMatchWrites = new Writes[Bracket[PingpongMatch]] {
      override def writes(bracket: Bracket[PingpongMatch]): JsValue = bracket match {
        case n: BracketNode[PingpongMatch] => Json.toJson(SiteBracket.convertNodeToSiteMatchNode(n))(nodeWrites).asInstanceOf[JsObject]
        case l: BracketLeaf[PingpongMatch] => Json.toJson(l)(leafWrites)
      }
    }

    implicit val siteRobinRoundWrites: Writes[SiteRobinRound] = (
      (__ \ "id").write[String] and
        (__ \ "numberOfRobinGroups").write[Int] and
        (__ \ "seriesId").write[String] and
        (__ \ "roundNr").write[Int] and
        (__ \ "robinRounds").lazyWrite[List[RobinGroup]](Writes.list[RobinGroup](Json.writes[RobinGroup])) and
          (__ \ "scoreKey").writeNullable[List[Int]] and
        (__ \ "roundType").write[String]
      ) (unlift(SiteRobinRound.unapply))

    implicit val siteRobinRoundReads: Reads[SiteRobinRound] = (
      (__ \ "id").read[String] and
        (__ \ "numberOfRobinGroups").read[Int] and
        (__ \ "seriesId").read[String] and
        (__ \ "roundNr").read[Int] and
        (__ \ "robinRounds").lazyRead[List[RobinGroup]](Reads.list[RobinGroup](Json.reads[RobinGroup])) and
        (__ \ "scoreKey").readNullable[List[Int]]
      ) (SiteRobinRound.apply(_, _, _, _, _, _))

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
