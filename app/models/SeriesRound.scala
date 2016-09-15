package models

import play.api.libs.json.{JsObject, JsResult, JsValue, Json}
import slick.jdbc.GetResult

sealed trait SeriesRound{
  def id: String
  def roundNr: Int
}

case class SiteBracketRound(id: String, numberOfBracketRounds: Int, seriesId: String, roundNr: Int) extends SeriesRound{
  def getId(m: SiteBracketRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteBracketRound): SiteBracketRound = m.copy(id = id)

  def writes(o: SiteBracketRound): JsObject = Json.format[SiteBracketRound].writes(o)

  def reads(json: JsValue): JsResult[SiteBracketRound] = Json.format[SiteBracketRound].reads(json)
}
case class SiteRobinRound(id: String, numberOfRobinGroups: Int, seriesId: String, roundNr: Int) extends SeriesRound{
  def getId(m: SiteRobinRound): Option[String] = Some(id)

  def setId(id: String)(m: SiteRobinRound): SiteRobinRound = m.copy(id = id)

  def writes(o: SiteRobinRound): JsObject = Json.format[SiteRobinRound].writes(o)

  def reads(json: JsValue): JsResult[SiteRobinRound] = Json.format[SiteRobinRound].reads(json)
}

case class GenericSeriesRound(seriesRoundId: String, numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int)
object SeriesRoundEvidence{
  implicit object seriesRoundIsModel extends Model[SeriesRound] {
    override def getId(m: SeriesRound): Option[String] = m match{
      case r:SiteRobinRound => r.getId(r)
      case b: SiteBracketRound => b.getId(b)
    }

    override def setId(id: String)(m: SeriesRound): SeriesRound = m match{
      case r:SiteRobinRound => r.setId(id)(r)
      case b: SiteBracketRound => b.setId(id)(b)
    }

    override def writes(o: SeriesRound): JsObject = o match{
      case r:SiteRobinRound => r.writes(r)
      case b: SiteBracketRound => b.writes(b)
    }

    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match{
      case "R" => Json.format[SiteRobinRound].reads(json)
      case "B" => Json.format[SiteBracketRound].reads(json)
    }
  }

  implicit object seriesRoundIsCrudable extends Crudable[GenericSeriesRound]{
    override def getId(crudable: GenericSeriesRound): String =  crudable.seriesRoundId

    override implicit val getResult: GetResult[GenericSeriesRound] = GetResult(r => GenericSeriesRound(r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  }
}
