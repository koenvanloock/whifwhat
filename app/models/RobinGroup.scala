package models

import models.matches.SiteMatch
import models.player.SeriesRoundPlayer


case class RobinGroup(robinGroupId: String, robinPlayers: List[SeriesRoundPlayer], robinMatches: List[SiteMatch])


