package services

import java.util.UUID

import models._

import scala.concurrent.Future

class DrawService {

  def drawRobins(robinPlayers: List[Player], numberOfRobins: Int, setTargetScore: Int, numberOfSetsToWin: Int): List[RobinGroup] ={

    val ids = for(robinNr <- (0 until numberOfRobins).toList) yield{(robinNr,UUID.randomUUID().toString)}
    ids.map{ tuple =>
     val playerGroup = robinPlayers.zipWithIndex.filter(couple => couple._2 % numberOfRobins == tuple._1).map( couple => RobinPlayer(UUID.randomUUID().toString, tuple._2, couple._1.playerId, couple._1.rank.value,tuple._1,0,0,0,0,0,0))
        val matches = createRobinMatches(tuple._2,playerGroup,numberOfRobins,setTargetScore)
        RobinGroup(tuple._2,playerGroup,matches)
    }
  }

  def createRobinMatches(robinId: String, playersList: List[RobinPlayer], numberOfSetsToWin: Int, setTargetScore: Int): List[SiteMatchWithGames] = {
     playersList.init.zipWithIndex.flatMap{ playerWithIndex =>
        val playerA = playerWithIndex._1
        playersList.drop(playerWithIndex._2 + 1).map { playerB => {
          val relHandicap = playerA.rankValue - playerB.rankValue
          val isForB = relHandicap > 0
           val siteMatch = SiteMatch(UUID.randomUUID().toString, playerA.seriesPlayerId, playerB.seriesPlayerId, Math.abs(relHandicap), isForB, setTargetScore, numberOfSetsToWin)
            val matchToInsert =  RobinMatch(UUID.randomUUID().toString, robinId, siteMatch.matchId)
                val sets=  for(setNr <- 1 to (numberOfSetsToWin*2-1)) yield{SiteGame(UUID.randomUUID().toString,siteMatch.matchId,0,0,setNr)}
                SiteMatchWithGames(siteMatch.matchId, siteMatch.playerA, siteMatch.playerB, siteMatch.handicap, siteMatch.isHandicapForB, siteMatch.targetScore, siteMatch.numberOfSetsToWin, sets.toList)
              }
      }
    }
  }
}
