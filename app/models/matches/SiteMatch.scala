package models.matches

import models.player.{Player, Rank}
import models.{Crudable, Model}
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}
import slick.jdbc.GetResult
import utils.JsonUtils


case class SiteMatch(
                      id: String,
                      playerA: Option[Player],
                      playerB: Option[Player],
                      roundId: String,
                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int,
                      wonSetsA: Int,
                      wonSetsB: Int,
                      games: List[SiteGame])

object MatchEvidence{

  implicit object matchIsModel extends Model[SiteMatch]{
    override def getId(m: SiteMatch): Option[String] = Some(m.id)

    override def setId(id: String)(m: SiteMatch): SiteMatch = m.copy(id = id)

    implicit val rankFormat = Json.format[Rank]
    implicit val playerFormat = Json.format[Player]
    val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
    implicit val gameWrites = Json.format[SiteGame]
    override def writes(o: SiteMatch): JsObject = Json.format[SiteMatch].writes(o)

    override def reads(json: JsValue): JsResult[SiteMatch] = Json.format[SiteMatch].reads(json)
  }

  implicit object MatchIsCrudable extends Crudable[SiteMatch]{
    override def getId(crudable: SiteMatch): String = crudable.id
    override implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<,None,None,r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, List()))
  }
}

object MatchChecker{
  def isWon(siteMatch: SiteMatch): Boolean = {
    (siteMatch.wonSetsA > siteMatch.wonSetsB) && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin ||
      siteMatch.wonSetsA < siteMatch.wonSetsB && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin
  }
}
