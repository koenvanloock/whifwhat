package models.player

case class BracketPlayer(id: String, bracketId: String, playerId: String, firstname: String, lastname: String, rank: Rank, playerScores: PlayerScores)
