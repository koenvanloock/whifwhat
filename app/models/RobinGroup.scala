package models

import models.matches.SiteMatch
import models.player.SeriesPlayer


case class RobinGroup(robinGroupId: String, robinPlayers: List[SeriesPlayer], robinMatches: List[SiteMatch])


