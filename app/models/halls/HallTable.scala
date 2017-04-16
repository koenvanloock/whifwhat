package models.halls

import models.matches.SiteMatch
import models.player.Player

case class HallTable(row: Int, column: Int, siteMatch: Option[SiteMatch], referee: Option[Player])
