package jsonconverters

import java.time.LocalDate

import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries}
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, Rank}
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

  implicit val hallOverviewTournamentWrites: Writes[HallOverViewTournament] = (
    (__ \ "id").write[String] and
      (__ \ "tournamentName").write[String] and
      (__ \ "tournamentDate").write[LocalDate] and
      (__ \ "series").lazyWrite[List[HallOverviewSeries]](Writes.list[HallOverviewSeries](Json.writes[HallOverviewSeries])) and
      (__ \ "freePlayers").lazyWrite[List[Player]](Writes.list[Player](Json.writes[Player])) and
        (__ \ "remainingMatches").lazyWrite[List[PingpongMatch]](Writes.list[PingpongMatch](matchWrites))
    ) (unlift(HallOverViewTournament.unapply))

}
