package services

import java.util.UUID

import models._

import scala.util.{Try, Random}

class DrawService {

  def drawRobins(robinPlayers: List[Player], numberOfRobins: Int, setTargetScore: Int, numberOfSetsToWin: Int, drawtype: DrawType): Option[RobinRound] = {
    val sortedPlayers = drawtype match {
      case DrawTypes.EnteredOrder => robinPlayers
      case DrawTypes.RandomOrder => Random.shuffle(robinPlayers)
      case DrawTypes.RankedRandomOrder => robinPlayers.sortBy(-_.rank.value).grouped(numberOfRobins).map(placeList => Random.shuffle(placeList)).toList.flatten
    }
    drawSortedRobins(sortedPlayers, numberOfRobins, setTargetScore, numberOfSetsToWin)
  }

  def drawSortedRobins(robinPlayers: List[Player], numberOfRobins: Int, setTargetScore: Int, numberOfSetsToWin: Int): Option[RobinRound] = {
    if (numberOfRobins > 0 && numberOfRobins < robinPlayers.length / 2) {
      val ids = for (robinNr <- (0 until numberOfRobins).toList) yield (robinNr, UUID.randomUUID().toString)
      val robins = ids.map { tuple =>
        val playerGroup = robinPlayers.zipWithIndex.filter(couple => couple._2 % numberOfRobins == tuple._1).map(couple => RobinPlayer(UUID.randomUUID().toString, tuple._2, couple._1.playerId, couple._1.firstname, couple._1.lastname, couple._1.rank, tuple._1, 0, 0, 0, 0, 0, 0))
        val matches = createRobinMatches(tuple._2, playerGroup, numberOfRobins, setTargetScore)
        RobinGroup(tuple._2, playerGroup, matches)
      }
      Some(RobinRound(robins))
    } else None
  }

  def createRobinMatches(robinId: String, playersList: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): List[SiteMatchWithGames] = {
    playersList.init.zipWithIndex.flatMap { playerWithIndex =>
      val playerA = playerWithIndex._1
      playersList.drop(playerWithIndex._2 + 1).map { playerB => {
        val relHandicap = playerA.rank.value - playerB.rank.value
        val isForB = relHandicap > 0
        val siteMatch = SiteMatch(UUID.randomUUID().toString, playerA.seriesPlayerId, playerB.seriesPlayerId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin,0,0)
        val sets = for (setNr <- 1 to (numberOfSetsToWin * 2 - 1)) yield {
          SiteGame(UUID.randomUUID().toString, siteMatch.matchId, 0, 0, setNr)
        }
        SiteMatchWithGames(siteMatch.matchId, siteMatch.playerA, siteMatch.playerB, siteMatch.handicap, siteMatch.isHandicapForB, siteMatch.targetScore, siteMatch.numberOfSetsToWin, 0,0, sets.toList)
      }
      }
    }
  }

  def drawEmptyMatches(roundNr: Int, bracketId: String, numberOfBracketRounds: Int, numberOfSetsToWin: Int, setTargetScore: Int): List[BracketMatch] = {
    (0 to Math.pow(2, numberOfBracketRounds - roundNr - 1).toInt).toList.map { matchNr =>
      createBracketMatch(roundNr, bracketId, matchNr + 1, numberOfSetsToWin, setTargetScore, None, None)
    }

  }

  def drawFirstRound(roundNr: Int, bracketId: String, bracketPlayers: List[Player], numberOfBracketRounds: Int, numberOfSetsToWin: Int, setTargetScore: Int): List[BracketMatch] = {
    numberOfBracketRounds match {
      case 4 => List((1, 0, 15), (2, 8, 7), (3, 4, 11), (4, 12, 3), (5, 2, 13), (6, 10, 5), (7, 6, 9), (8, 14, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case 3 => List((1, 0, 7), (1, 4, 3), (1, 2, 5), (1, 6, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case 2 => List((1, 0, 3), (1, 2, 1)).map(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers))
      case _ => List(createMatchFromPlayerIndex(roundNr, bracketId, numberOfSetsToWin, setTargetScore, bracketPlayers)(1, 0, 1))
    }
  }

  def createMatchFromPlayerIndex(roundNr: Int, bracketId: String, numberOfSetsToWin: Int, setTargetScore: Int, bracketPlayers: List[Player]): ((Int, Int, Int)) => BracketMatch = {
    tuple => createBracketMatch(roundNr, bracketId, tuple._1, numberOfSetsToWin, setTargetScore, bracketPlayers.drop(tuple._2).headOption, bracketPlayers.drop(tuple._3).headOption)
  }

  def createBracketMatch(roundNr: Int, bracketId: String, matchId: Int, numberOfSetsToWin: Int, setTargetScore: Int, playerA: Option[Player], playerB: Option[Player]): BracketMatch = {
    val relHandicap = Try(playerA.get.rank.value - playerB.get.rank.value).toOption.getOrElse(0)
    val isForB = relHandicap > 0
    BracketMatch(UUID.randomUUID().toString, bracketId, roundNr, matchId, UUID.randomUUID().toString, playerA.map(_.playerId), playerB.map(_.playerId), relHandicap, isForB, numberOfSetsToWin, setTargetScore)
  }

  def convertToBracketPlayer(bracketId: String): (Player) => BracketPlayer = player => BracketPlayer(bracketId, player.playerId, player.firstname, player.lastname, player.rank, 0, 0, 0, 0, 0, 0)

  def drawBracket(players: List[Player], numberOfBracketRounds: Int, numberOfSetsToWin: Int, setTargetScore: Int): Option[Bracket] = {

    if (numberOfBracketRounds > 0) {
      val bracketId = UUID.randomUUID().toString
      val bracketRounds = (0 until numberOfBracketRounds).toList.map { roundNr =>
        roundNr match {
          case 0 => drawFirstRound(roundNr + 1, bracketId, players, numberOfBracketRounds, numberOfSetsToWin, setTargetScore)
          case _ => drawEmptyMatches(roundNr + 1, bracketId, numberOfBracketRounds, numberOfSetsToWin, setTargetScore)
        }
      }
      val bracketPlayers = players.take(Math.pow(2, numberOfBracketRounds).toInt).map {
        convertToBracketPlayer(bracketId)
      }
      Some(Bracket(UUID.randomUUID().toString, bracketPlayers, bracketRounds))

    } else None
  }
}

case class DrawType(name: String)

object DrawTypes {
  val EnteredOrder = DrawType("EnteredOrder")
  val RandomOrder = DrawType("Random")
  val RankedRandomOrder = DrawType("RankedRandom")
}