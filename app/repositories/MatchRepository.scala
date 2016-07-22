package repositories

import javax.inject.Inject

import models.matches.SiteMatch
import models.tablemodels.MatchTable
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery

import models.tablemodels.MatchTableTableModel._
import models.matches.MatchEvidence._

class MatchRepository @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[SiteMatch, MatchTable](dbConfigProvider = dbConfigProvider)("MATCHES"){
  override def query: driver.api.TableQuery[MatchTable] = TableQuery[MatchTable]
}
