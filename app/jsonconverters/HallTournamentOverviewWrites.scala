package jsonconverters

import java.time.LocalDate

import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries}
import models.matches.{PingpongGame, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, Rank, RefereeInfo, ViewablePlayer}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Writes, __}
import utils.JsonUtils

object HallTournamentOverviewWrites{

  implicit val rankWrites = Json.writes[Rank]
  implicit val playerWrites = Json.writes[Player]
  implicit val optionWrites = JsonUtils.optionWrites[Player]
  implicit val gameWrites = Json.writes[PingpongGame]
  implicit val matchWrites = Json.writes[PingpongMatch]
  implicit val matchListWrites = JsonUtils.listWrites(matchWrites)
  implicit val hallRoundWrites = Json.writes[HallOverviewRound]
  implicit val refereeInfoWrites = Json.writes[RefereeInfo]

  implicit val hallOverviewTournamentWrites: Writes[HallOverViewTournament] = (
    (__ \ "id").write[String] and
      (__ \ "tournamentName").write[String] and
      (__ \ "tournamentDate").write[LocalDate] and
      (__ \ "series").lazyWrite[List[HallOverviewSeries]](Writes.list[HallOverviewSeries](Json.writes[HallOverviewSeries])) and
      (__ \ "freePlayers").lazyWrite[List[ViewablePlayer]](Writes.list[ViewablePlayer](Json.writes[ViewablePlayer])) and
        (__ \ "remainingMatches").lazyWrite[List[ViewablePingpongMatch]](Writes.list[ViewablePingpongMatch](Json.writes[ViewablePingpongMatch]))
    ) (unlift(HallOverViewTournament.unapply))

}
