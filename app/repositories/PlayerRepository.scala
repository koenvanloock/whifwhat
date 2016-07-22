package repositories

import javax.inject.Inject

import models.Role
import models.player.Player
import models.tablemodels.{PlayerTable, RolesTable}
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.lifted.TableQuery
import models.tablemodels.PlayerTableTableModel._
import models.player.PlayerEvidence._


class PlayerRepository @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[Player, PlayerTable](dbConfigProvider = dbConfigProvider)("PLAYERS"){
  override def query: driver.api.TableQuery[PlayerTable] = TableQuery[PlayerTable]
}
