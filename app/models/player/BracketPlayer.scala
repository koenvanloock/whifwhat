package models.player

case class BracketPlayer(bracketId: String, playerId: String, firstname: String, lastname: String, rank: Rank, playerScores: PlayerScores)
