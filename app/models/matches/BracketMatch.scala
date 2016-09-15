package models.matches

import models.player.Player


case class BracketMatch(bracketMatchId: String, bracketId: String, bracketRoundNr: Int, bracketMatchNr: Int, siteMatch: SiteMatch)
