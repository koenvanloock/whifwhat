package models

import models.player.Rank


case class TournamentPlayer(id: String, firstname: String, lastname: String, tournamentRank: Rank, playerId: String, tournamentId: String)
