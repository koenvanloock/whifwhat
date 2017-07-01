package models

import models.matches.PingpongMatch
import models.player.SeriesPlayer


case class RobinGroup(robinGroupId: String, robinPlayers: List[SeriesPlayer], robinMatches: List[PingpongMatch])


