package repositories
import javax.inject.Inject

import models.tablemodels._
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import _root_.slick.driver.JdbcProfile
import play.db.NamedDatabase
import slick.jdbc.SQLActionBuilder

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

class Schema @Inject()(@NamedDatabase("default") override protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import _root_.slick.profile.{FixedSqlStreamingAction, SqlStreamingAction, RelationalProfile, FixedSqlAction}


  def initSchema() = {
    import driver.api._

    val genericSeriesRoundSchema = TableQuery[GenericSeriesRoundTable].schema
    val playerSchema = TableQuery[PlayerTable].schema
    val roleSchema = TableQuery[RolesTable].schema
    val seriesPlayerSchema = TableQuery[SeriesPlayerTable].schema
    val seriesSchema = TableQuery[SeriesTable].schema
    val tournamentSchema = TableQuery[TournamentTable].schema
    val userTable = TableQuery[UserTable].schema
    val matchTable = TableQuery[MatchTable].schema

    val schema =  genericSeriesRoundSchema ++
                  playerSchema ++
                  seriesPlayerSchema ++
                  seriesSchema ++
                  tournamentSchema ++
                  matchTable

    schema.createStatements.foreach { s =>
      //println(s)
      Await.result(
        db.run(DBIO.seq(sqlu"#$s")) recover {
          case NonFatal(cause) =>
            //println(s"SOMETHING WENT WRONG: $cause")
            Future.successful(())
        },
        10000 millis)

    }
  }

}
