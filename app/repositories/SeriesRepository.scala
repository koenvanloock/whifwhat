package repositories

import javax.inject.Inject

import models.{TournamentSeriesResultModel, TournamentSeries}
import models.tablemodels.SeriesTable
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery
import TournamentSeriesResultModel._
import models.tablemodels.SeriesTableTableModel._

class SeriesRepository @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[TournamentSeries, SeriesTable](dbConfigProvider = dbConfigProvider)("TOURNAMENT_SERIES"){
  override def query: driver.api.TableQuery[SeriesTable] = TableQuery[SeriesTable]
}