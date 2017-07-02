package models.halls

import models.matches.PingpongMatch
import models.player.Player

case class HallTable(
                      hallId: String,
                      row: Int,
                      column: Int,
                      pingpongMatch: Option[PingpongMatch],
                      referee: Option[Player],
                      horizontal: Boolean=false,
                      hidden: Boolean =false,
                      isGreen: Boolean = true,
                      tableName: String = "")
