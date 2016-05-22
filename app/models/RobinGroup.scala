package models

import models.matches.SiteMatchWithGames
import models.player.SeriesRoundPlayer

/**
  * @author Koen Van Loock
  * @version 1.0 1/05/2016 18:44
  */


case class RobinGroup(robinGroupId: String, robinPlayers: List[SeriesRoundPlayer], robinMatches: List[SiteMatchWithGames])


