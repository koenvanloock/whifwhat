package models.player

case class SeriesRoundPlayer(id: String, seriesPlayerId: String, seriesRoundId: String, firstname: String, lastname: String, rank: Rank, playerScores: PlayerScores)
