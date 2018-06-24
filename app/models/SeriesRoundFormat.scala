package models

import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Rank, SeriesPlayer}
import models.types.{Bracket, BracketLeaf, BracketNode, SiteMatchNode}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.JsonUtils

object SeriesRoundFormat {
  implicit val rankFormat = Json.format[Rank]
  implicit val playerScoresFormat = Json.format[PlayerScores]
  implicit val playerFormat = Json.format[Player]
  implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
  implicit val gameWrites = Json.format[PingpongGame]
  implicit val matchFormat = Json.format[PingpongMatch]
  implicit val listSeriesPlayerWrites = JsonUtils.listWrites(Json.writes[SeriesPlayer])
  implicit val listSeriesPlayerReads = JsonUtils.listReads(Json.reads[SeriesPlayer])


  implicit val siteBracketRoundWrites: Writes[SiteBracketRound] = (
    (__ \ "_id").write[String] and
      (__ \ "numberOfBracketRounds").write[Int] and
      (__ \ "seriesId").write[String] and
      (__ \ "roundNr").write[Int] and
      (__ \ "bracketPlayers").lazyWrite[List[SeriesPlayer]](Writes.list[SeriesPlayer](Json.writes[SeriesPlayer])) and
      (__ \ "bracketRounds").lazyWrite[Bracket[PingpongMatch]](bracketSiteMatchWrites) and
      (__ \ "scoreKey").writeNullable[List[Int]] and
      (__ \ "roundType").write[String]
    ) (unlift(SiteBracketRound.unapply))


  implicit val siteBracketRoundReads: Reads[SiteBracketRound] = (
    (__ \ "_id").read[String] and
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
    ) (unlift(SiteMatchNode.unapply))

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
    (__ \ "_id").write[String] and
      (__ \ "numberOfRobinGroups").write[Int] and
      (__ \ "seriesId").write[String] and
      (__ \ "roundNr").write[Int] and
      (__ \ "robinRounds").lazyWrite[List[RobinGroup]](Writes.list[RobinGroup](Json.writes[RobinGroup])) and
      (__ \ "scoreKey").writeNullable[List[Int]] and
      (__ \ "roundType").write[String]
    ) (unlift(SiteRobinRound.unapply))

  implicit val siteRobinRoundReads: Reads[SiteRobinRound] = (
    (__ \ "_id").read[String] and
      (__ \ "numberOfRobinGroups").read[Int] and
      (__ \ "seriesId").read[String] and
      (__ \ "roundNr").read[Int] and
      (__ \ "robinRounds").lazyRead[List[RobinGroup]](Reads.list[RobinGroup](Json.reads[RobinGroup])) and
      (__ \ "scoreKey").readNullable[List[Int]]
    ) (SiteRobinRound.apply(_, _, _, _, _, _))

  implicit val swissLegReads: Reads[SwissLeg] = (
    (__ \ "id").read[String] and
      (__ \ "matches").lazyRead[List[PingpongMatch]](Reads.list[PingpongMatch](matchFormat))
    ) (SwissLeg.apply(_, _))

  implicit val swissLegWrites: Writes[SwissLeg] = (
    (__ \ "id").write[String] and
      (__ \ "matches").lazyWrite[List[PingpongMatch]](Writes.list[PingpongMatch](matchFormat))
    ) (unlift(SwissLeg.unapply))

  implicit val swissRoundWrites: Writes[SwissRound] = (
    (__ \ "_id").write[String] and
      (__ \ "seriesId").write[String] and
      (__ \ "roundNr").write[Int] and
      (__ \ "swisslegs").lazyWrite[List[SwissLeg]](Writes.list[SwissLeg](swissLegWrites)) and
      (__ \ "players").lazyWrite[List[SeriesPlayer]](Writes.list[SeriesPlayer](Json.writes[SeriesPlayer])) and
      (__ \ "scoreKey").writeNullable[List[Int]] and
      (__ \ "roundType").write[String]
    ) (unlift(SwissRound.unapply))


  implicit val swissRoundReads: Reads[SwissRound] = (
    (__ \ "_id").read[String] and
      (__ \ "seriesId").read[String] and
      (__ \ "roundNr").read[Int] and
      (__ \ "swisslegs").lazyRead[List[SwissLeg]](Reads.list[SwissLeg](swissLegReads)) and
      (__ \ "players").lazyRead[List[SeriesPlayer]](Reads.list[SeriesPlayer](Json.reads[SeriesPlayer])) and
      (__ \ "scoreKey").readNullable[List[Int]]
    ) (SwissRound.apply(_, _, _, _, _, _))

  def writes(o: SeriesRound): JsObject = o match {
    case r: SiteRobinRound => Json.toJson(r)(siteRobinRoundWrites).asInstanceOf[JsObject]
    case b: SiteBracketRound => Json.toJson(b)(siteBracketRoundWrites).asInstanceOf[JsObject]
    case s: SwissRound => Json.toJson(s)(swissRoundWrites).asInstanceOf[JsObject]
  }

  def reads(json: JsValue): JsResult[SeriesRound] = Json.fromJson[SeriesRound](json)(roundReads)


  implicit val roundReads = new Reads[SeriesRound] {
    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => Json.fromJson(json)(siteRobinRoundReads)
      case "B" => Json.fromJson(json)(siteBracketRoundReads)
      case "S" => Json.fromJson(json)(swissRoundReads)
    }
  }
}
