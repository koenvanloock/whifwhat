package models.matches


case class BracketMatchWithGames(bracketMatchId: String, bracketId: String, bracketRoundNr: Int, bracketMatchNr: Int, matchId: String, playerA: Option[String], playerB: Option[String], handicap: Int, isHandicapForB: Boolean, targetScore: Int, numberOfSetsToWin: Int, sets: List[SiteGame])
