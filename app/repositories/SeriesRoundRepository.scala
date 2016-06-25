package repositories

import com.google.inject.Inject
import models.GenericSeriesRound
import models.tablemodels.GenericSeriesRoundTable
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import models.GenericSeriesRoundIsCrudable._
import models.tablemodels.GenericSeriesRoundTableModel._

class SeriesRoundRepository @Inject()(@NamedDatabase("default") override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[GenericSeriesRound, GenericSeriesRoundTable](dbConfigProvider = dbConfigProvider)("SERIES_ROUNDS"){
  override def query: driver.api.TableQuery[GenericSeriesRoundTable] = TableQuery[GenericSeriesRoundTable]
}
