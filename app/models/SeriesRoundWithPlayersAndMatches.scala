package models

import models.matches.BracketMatchWithGames
import models.player.BracketPlayer

/**
  * @author Koen Van Loock
  * @version 1.0 8/05/2016 20:31
  */
sealed trait SeriesRoundWithPlayersAndMatches {

}

case class Bracket (bracketId: String, bracketPlayers: List[BracketPlayer], bracketRounds: List[List[BracketMatchWithGames]]) extends SeriesRoundWithPlayersAndMatches
case class RobinRound(robinList : List[RobinGroup]) extends SeriesRoundWithPlayersAndMatches