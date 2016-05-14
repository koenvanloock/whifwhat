package models


case class BracketPlayer(bracketId: String, playerId: String, firstname: String, lastname: String, rank: Rank, wonmatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int)
