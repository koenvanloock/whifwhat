package utils

import models.matches.PingpongMatch
import models.player.Player

object PingpongMatchUtils {

  def getMatchPlayers(pingpongMatch: PingpongMatch): List[Player] =
    pingpongMatch.playerA.toList ++ pingpongMatch.playerB.toList
}
