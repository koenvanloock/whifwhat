package utils

import models.player.{Player, Rank}

object HandicapCalculator {

  def calculateHandicap(rankA: Rank, rankB: Rank, setTargetScore: Int): Int = if(setTargetScore == 21){
    rankA.value - rankB.value
  } else {
    val subtractedRanksHalved = (rankA.value - rankB.value) / 2
    if(subtractedRanksHalved > 0) Math.floor(subtractedRanksHalved).toInt
    else Math.ceil(subtractedRanksHalved).toInt
  }

  def calculateHandicapWithOpts(playerA: Option[Player], playerB: Option[Player], setTargetScore: Int): Int = (for {
    realPlayerA <- playerA
    realPlayerB <- playerB
  } yield calculateHandicap(realPlayerA.rank, realPlayerB.rank, setTargetScore)).getOrElse(0)
}
