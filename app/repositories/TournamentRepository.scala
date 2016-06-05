package repositories

import javax.inject.Inject

import models.{TournamentResultModel, Tournament}
import models.tablemodels.{TournamentTableTableModel, TournamentTable}
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.lifted.TableQuery

import TournamentTableTableModel._
import TournamentResultModel._

class TournamentRepository  @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[Tournament, TournamentTable](dbConfigProvider = dbConfigProvider)("TOURNAMENTS"){
  override def query: driver.api.TableQuery[TournamentTable] = TableQuery[TournamentTable]
}