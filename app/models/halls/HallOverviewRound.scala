package models.halls

import java.time.LocalDate

import models.matches.{PingpongMatch, PingpongGame}
import models.player.{Player, Rank}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Json, Writes, __}
import play.api.libs.functional.syntax._
import utils.JsonUtils


case class HallOverViewTournament(id: String, tournamentName: String, tournamentDate: LocalDate, series: List[HallOverviewSeries], freePlayers: List[Player])
case class HallOverviewSeries(seriesId: String, seriesName: String, seriesColor: String, rounds: List[HallOverviewRound])
case class HallOverviewRound(roundId: String, seriesId: String, roundName: String, roundMatchesToPlay: List[PingpongMatch], completePercentage: Double)



object HallOverviewWrites{

  implicit val rankWrites = Json.writes[Rank]
  implicit val playerWrites = Json.writes[Player]
  implicit val optionWrites = JsonUtils.optionWrites[Player]
  implicit val gameWrites = Json.writes[PingpongGame]
  implicit val matchWrites = Json.writes[PingpongMatch]
  implicit val matchListWrites = JsonUtils.listWrites(matchWrites)
  implicit val hallRoundWrites = Json.writes[HallOverviewRound]

  val hallOverviewTournamentWrites: Writes[HallOverViewTournament] = (
    (__ \ "id").write[String] and
      (__ \ "tournamentName").write[String] and
      (__ \ "tournamentDate").write[LocalDate] and
      (__ \ "series").lazyWrite[List[HallOverviewSeries]](Writes.list[HallOverviewSeries](Json.writes[HallOverviewSeries])) and
      (__ \ "freePlayers").lazyWrite[List[Player]](Writes.list[Player](Json.writes[Player]))
    ) (unlift(HallOverViewTournament.unapply))

}