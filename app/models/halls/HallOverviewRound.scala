package models.halls

import java.time.LocalDate

import models.matches.{PingpongGame, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, Rank, ViewablePlayer}


case class HallOverViewTournament(id: String, tournamentName: String, tournamentDate: LocalDate, series: List[HallOverviewSeries], players: List[ViewablePlayer], matchesToPlay: List[ViewablePingpongMatch])
case class HallOverviewSeries(seriesId: String, seriesName: String, seriesColor: String, rounds: List[HallOverviewRound])
case class HallOverviewRound(roundId: String, seriesId: String, roundName: String, completePercentage: Double)
