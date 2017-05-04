package services

import java.util.UUID
import javax.inject.Inject

import models._
import models.matches.{SiteGame, PingpongMatch}
import models.player._

import scala.util.{Random, Try}

class DrawService @Inject()() {

  def drawRobins(robinPlayers: List[SeriesPlayer], robinRound: SiteRobinRound, setTargetScore: Int, numberOfSetsToWin: Int, drawtype: DrawType): Option[SiteRobinRound] = {
    val sortedPlayers = drawtype match {
      case DrawTypes.EnteredOrder => robinPlayers
      case DrawTypes.RandomOrder => Random.shuffle(robinPlayers)
      case DrawTypes.RankedRandomOrder => robinPlayers.sortBy(-_.player.rank.value).grouped(robinRound.numberOfRobinGroups).map(placeList => Random.shuffle(placeList)).toList.flatten
    }
    drawSortedRobins(sortedPlayers, robinRound, setTargetScore, numberOfSetsToWin)
  }

  def drawSortedRobins(robinPlayers: List[SeriesPlayer], robinRound: SiteRobinRound, setTargetScore: Int, numberOfSetsToWin: Int): Option[SiteRobinRound] = {
    if (robinRound.numberOfRobinGroups > 0 && robinRound.numberOfRobinGroups <= robinPlayers.length / 2) {
      val ids = for (robinNr <- (0 until robinRound.numberOfRobinGroups).toList) yield (robinNr, UUID.randomUUID().toString)
      val robins: List[RobinGroup] = ids.map { tuple =>
        val playerGroup = robinPlayers.zipWithIndex.filter(couple => couple._2 % robinRound.numberOfRobinGroups == tuple._1).map(couple => SeriesPlayer(UUID.randomUUID().toString, couple._1.id, couple._1.player, PlayerScores()))
        val matches = createRobinMatches(tuple._2, playerGroup, numberOfSetsToWin, setTargetScore)
        RobinGroup(tuple._2, playerGroup, matches)
      }
      Some(robinRound.copy(robinList = robins))
    } else None
  }

  def createRobinMatches(robinId: String, playersList: List[SeriesPlayer], numberOfSetsToWin: Int, setTargetScore: Int): List[PingpongMatch] = {
    playersList.init.zipWithIndex.flatMap { playerWithIndex =>
      val playerA = playerWithIndex._1
      playersList.drop(playerWithIndex._2 + 1).map { playerB => {
        val relHandicap = playerA.player.rank.value - playerB.player.rank.value
        val isForB = relHandicap > 0
        val sets = createSiteGameForMatch(numberOfSetsToWin)
        PingpongMatch(UUID.randomUUID().toString, Some(playerA.player), Some(playerB.player), robinId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin, 0, 0, sets)
      }
      }
    }
  }

  def createSiteGameForMatch(numberOfSetsToWin: Int): List[SiteGame] = {
    for (setNr <- (1 to (numberOfSetsToWin * 2 - 1)).toList) yield {
      SiteGame(0, 0, setNr)
    }
  }

  def drawEmptyMatches(roundNr: Int, bracketId: String, numberOfBracketRounds: Int, numberOfSetsToWin: Int, setTargetScore: Int): List[PingpongMatch] = {
    (0 to Math.pow(2, numberOfBracketRounds - roundNr - 1).toInt).toList.map { matchNr =>
      createBracketMatch(roundNr, bracketId, matchNr + 1, numberOfSetsToWin, setTargetScore, None, None)
    }

  }

  def drawFirstRoundOfBracket(roundNr: Int, bracketId: String, bracketPlayers: List[SeriesPlayer], numberOfBracketRounds: Int, numberOfSetsToWin: Int, setTargetScore: Int): List[PingpongMatch] = {
    numberOfBracketRounds match {
      case 4 => List((1, 0, 15), (2, 8, 7), (3, 4, 11), (4, 12, 3), (5, 2, 13), (6, 10, 5), (7, 6, 9), (8, 14, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case 3 => List((1, 0, 7), (1, 4, 3), (1, 2, 5), (1, 6, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case 2 => List((1, 0, 3), (1, 2, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case _ => List(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers)(1, 0, 1))
    }
  }

  def createMatchFromPlayerIndex(roundNr: Int, bracketId: String, numberOfSetsToWin: Int, setTargetScore: Int, bracketPlayers: List[SeriesPlayer]): ((Int, Int, Int)) => PingpongMatch = {
    tuple => createBracketMatch(roundNr, bracketId, tuple._1, numberOfSetsToWin, setTargetScore, bracketPlayers.drop(tuple._2).headOption, bracketPlayers.drop(tuple._3).headOption)
  }

  def createBracketMatch(roundNr: Int, bracketId: String, matchNr: Int, numberOfSetsToWin: Int, setTargetScore: Int, playerA: Option[SeriesPlayer], playerB: Option[SeriesPlayer]): PingpongMatch = {
    val relHandicap = Try(playerA.get.player.rank.value - playerB.get.player.rank.value).toOption.getOrElse(0)
    val isForB = relHandicap > 0
    val matchId = UUID.randomUUID().toString
    val sets = createSiteGameForMatch(numberOfSetsToWin)
    PingpongMatch(UUID.randomUUID().toString, playerA.map(_.player), playerB.map(_.player), bracketId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin, 0, 0, sets)
  }

  def convertToBracketPlayer(bracketId: String): (SeriesPlayer) => SeriesPlayer = seriesPlayer => SeriesPlayer(UUID.randomUUID().toString, bracketId, seriesPlayer.player, PlayerScores())

  def drawBracket(players: List[SeriesPlayer], bracket: SiteBracketRound, numberOfSetsToWin: Int, setTargetScore: Int): Option[SiteBracketRound] = {

    if (bracket.numberOfBracketRounds > 0) {
      val bracketId = UUID.randomUUID().toString
      val bracketRounds = (0 until bracket.numberOfBracketRounds).toList.map(drawBracketRound(bracketId, players, numberOfSetsToWin, setTargetScore, bracket))
      val bracketPlayers = players.take(Math.pow(2, bracket.numberOfBracketRounds).toInt).map {
        convertToBracketPlayer(bracketId)
      }
      val firstRoundMatches = drawFirstRoundOfBracket(bracket.numberOfBracketRounds, bracketId, bracketPlayers, bracket.numberOfBracketRounds, numberOfSetsToWin, setTargetScore)
      Some(bracket.copy(bracketPlayers = bracketPlayers, bracket = SiteBracket.createBracket(firstRoundMatches)))

    } else None
  }

  def drawBracketRound(bracketId: String, players: List[SeriesPlayer], numberOfSetsToWin: Int, setTargetScore: Int, bracket: SiteBracketRound): Int => List[PingpongMatch] = {
    case roundNr if (roundNr == 0) => drawFirstRoundOfBracket(roundNr + 1, bracketId, players, bracket.numberOfBracketRounds, numberOfSetsToWin, setTargetScore)
    case roundNr if (roundNr > 0) => drawEmptyMatches(roundNr + 1, bracketId, bracket.numberOfBracketRounds, numberOfSetsToWin, setTargetScore)
  }

  def drawSubsequentRound(seriesRound: SeriesRound, sortedPlayerList: List[SeriesPlayer], series: TournamentSeries): Option[SeriesRound] =  seriesRound match {
    case bracket: SiteBracketRound  => drawBracket(sortedPlayerList, bracket, series.numberOfSetsToWin, series.setTargetScore)
    case robinRound: SiteRobinRound => drawRobins(sortedPlayerList, robinRound, series.setTargetScore, series.numberOfSetsToWin, DrawTypes.EnteredOrder)
  }

}


case class DrawType(name: String)

object DrawTypes {
  val EnteredOrder = DrawType("EnteredOrder")
  val RandomOrder = DrawType("Random")
  val RankedRandomOrder = DrawType("RankedRandom")
}