package models

import models.matches.BracketMatchWithGames
import models.player.BracketPlayer

sealed trait SeriesRoundWithPlayersAndMatches {}

case class Bracket (bracketId: String, bracketPlayers: List[BracketPlayer], bracketRounds: List[List[BracketMatchWithGames]]) extends SeriesRoundWithPlayersAndMatches
case class RobinRound(robinList : List[RobinGroup]) extends SeriesRoundWithPlayersAndMatches