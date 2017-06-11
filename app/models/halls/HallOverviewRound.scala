package models.halls

import java.time.LocalDate

import models.matches.{PingpongMatch, PingpongGame}
import models.player.{Player, Rank}
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{Json, Writes, __}
import play.api.libs.functional.syntax._
import utils.JsonUtils


case class HallOverViewTournament(id: String, tournamentName: String, tournamentDate: LocalDate, series: List[HallOverviewSeries], freePlayers: List[Player], matchesToPlay: List[PingpongMatch])
case class HallOverviewSeries(seriesId: String, seriesName: String, seriesColor: String, rounds: List[HallOverviewRound])
case class HallOverviewRound(roundId: String, seriesId: String, roundName: String, completePercentage: Double)
