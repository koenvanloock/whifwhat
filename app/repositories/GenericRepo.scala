package repositories

import java.util.UUID

import _root_.slick.dbio.{Effect, NoStream}
import _root_.slick.driver.JdbcProfile
import _root_.slick.profile.{FixedSqlStreamingAction, SqlStreamingAction, RelationalProfile, FixedSqlAction}
import com.google.inject.Inject
import models.Crudable
import models.tablemodels.TableModel
import org.slf4j.{Logger, LoggerFactory}
import play.api.Play
import play.api.Play.current
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import services.GenericAtomicCrudService
import scala.collection.immutable.Map
import scala.concurrent.ExecutionContext.Implicits.global
import utils.FutureUtils._

import scala.concurrent.Future

abstract class GenericRepo [M: Crudable, TM<: RelationalProfile#API#Table[M]: TableModel]
(protected val dbConfigProvider: DatabaseConfigProvider)
(val name: String)
  extends HasDatabaseConfigProvider[JdbcProfile]
{

  import driver.api._

  private final val logger: Logger = LoggerFactory.getLogger(classOf[GenericRepo[M, TM]])

  implicit val model = implicitly[Crudable[M]]
  val tableModel = implicitly[TableModel[TM]]
  //
  // standard CRUD part
  //

  def query: TableQuery[TM]

  def create(m: M): Future[Option[M]] = {
    db.run(query += m).map { _ => ()
      logger.debug("create OK with id:" + m)
      Some(m)
    }
  }

  def createWithFixedId(m: M): Future[Option[M]] = {
    db.run(query += m).map { _ => ()
      Some(m)
    }

  }

  // Retrieve all

  def retrieveAllByFields(optionalFieldsMap: Option[Map[String, String]]): Future[List[M]] = {
    val msg = s"retrieveAllByFields( $optionalFieldsMap )"
    logger.debug(msg)
    val query = mkRetrieveAllByFieldsQuery(optionalFieldsMap)
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  def retrieveAll(): Future[List[M]] =
    retrieveAllByFields(None)

  def searchAllByField(fieldKey: String, fieldValue: String): Future[List[M]] =
    searchAllByFields(Map(fieldKey -> fieldValue))

  def searchAllByFields(fieldsMap: Map[String, String]): Future[List[M]] = {
    val msg = s"searchAllByFields( $fieldsMap )"
    logger.debug(msg)
    val query = mkSearchAllByFieldsQuery(fieldsMap)
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  def searchInFields(substringToSearch: String, fieldsToSearch: String*): Future[List[M]] = {
    val msg = s"searchAllByFields( $substringToSearch, $fieldsToSearch )"
    logger.debug(msg)
    val query = mkSearchMultipleColumns(substringToSearch, fieldsToSearch.toList)
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  def searchInFieldsWithIds(ids: List[String], substringToSearch: String, fieldsToSearch: String*): Future[List[M]] = {
    val msg = s"searchInFieldsWithId( $substringToSearch, $fieldsToSearch )"
    logger.debug(msg)
    val query = mkSearchMultipleColumsAndIds(ids, substringToSearch, fieldsToSearch.toList)
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  def retrieveAllByField(fieldKey: String, fieldValue: String): Future[List[M]] =
    retrieveAllByFields(Some(Map(fieldKey -> fieldValue)))

  // Retrieve (optionally) one

  def retrieveByFields(optionalFieldsMap: Option[Map[String, String]]): Future[Option[M]] =
    flatMapToOption("retrieve by fields")(retrieveAllByFields(optionalFieldsMap))

  def retrieveByField(fieldKey: String, fieldValue: String): Future[Option[M]] =
    flatMapToOption("retrieve by field")(retrieveAllByField(fieldKey, fieldValue))

  def retrieveById(id: String): Future[Option[M]] =
    flatMapToOption("retrieve by id")(retrieveAllByField("id", id))

  // Update

  def update(m: M): Future[Option[M]] ={
    val msg = s"\u001B[31mupdate($m)\u001B[30m"
    logger.debug(msg)
    val updater = {
      val compiledUpdateQuery = Compiled(query.filter { tm => tableModel.getRepId(tm) === model.getId(m)})
      compiledUpdateQuery.update(m)
    }
    logger.debug("\u001B[31mGenerated SQL statements: " + updater.statements + "\u001B[30m")
    db.run(updater).map { i =>
      if (i == 0) {
        val msg = s"nothing updated"
        logger.debug(msg)
        None
      } else {
        val msg = s"updated($m)"
        logger.debug(msg)
        Some(m)
      }
    }
  }

  // Delete

  def archive(id: String): Future[Option[Unit]] = {
    val msg = s"soft delete($id)"
    val deleter = for( row <- query if tableModel.getRepId(row) === id ) yield row
    val compiledDeleteQuery = Compiled(deleter).delete
    db.run(compiledDeleteQuery).map{ numberOfUpdatedRows =>
      if(numberOfUpdatedRows == 0) None else Some(())
    }
  }

  // FOR TESTING
  def delete(id: String): Future[Option[Unit]] = {
    val msg = s"delete($id)"
    logger.debug(msg)
    val deleter = query.filter { tm => tableModel.getRepId(tm) === id}
    val compiledDeleteQuery = Compiled(deleter).delete

    logger.debug("\u001B[31mGenerated SQL statements: " + compiledDeleteQuery.statements + "\u001B[30m")
    db.run(compiledDeleteQuery).map { i =>
      if (i == 0) {
        None
      } else {
        Some(())
      }
    }
  }

  // DeleteAll

  def deleteAll(): Future[Int]  = {
    val msg = s"deleteAll()"
    logger.debug(msg)
    val allDeleter = query.delete
    logger.debug("\u001B[31mGenerated SQL statements: " + allDeleter.statements + "\u001B[30m")
    db.run(allDeleter)
  }

  //
  // extra declared stuff
  //

  def retrieveAllByFieldsOrdered(optionalFieldsMap: Option[Map[String, String]],
                                 optionalOrder: Option[String] = Some("asc")): Future[List[M]] = {
    import tableModel._
    import model._
    val msg = s"retrieveAllByFieldsOrdered($optionalFieldsMap, $optionalOrder)"
    logger.debug(msg)
    val query = {
      optionalOrder.flatMap { order =>
        optionalFieldsMap.map { fieldsMap =>
          if (fieldsMap.isEmpty) {
            sql"SELECT * FROM #$name ORDER BY #$order".as[M]
          } else {
            val filters: String = fieldsMapToFilters(fieldsMap)
            sql"SELECT * FROM #$name WHERE #$filters ORDER BY #$order".as[M]
          }
        }
      } getOrElse (sql"SELECT * FROM #$name".as[M])
    }
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  def searchAllByFieldsOrdered(fieldsMap: Map[String, String],
                               optionalOrder: Option[String] = Some("asc")): Future[List[M]] = {
    import tableModel._
    import model._

    val msg = s"retrieveAllByFieldsOrdered($fieldsMap, $optionalOrder)"
    logger.debug(msg)
    val query = {
      optionalOrder.map { order =>
        val filters: String = fieldsMapToSearchFilters(fieldsMap)
        sql"SELECT * FROM #$name WHERE #$filters ORDER BY #$order".as[M]
      } getOrElse (sql"SELECT * FROM #$name".as[M])
    }
    logger.debug("\u001B[31mGenerated SQL statements: " + query.statements + "\u001B[30m")
    db.run(query).map(_.toList)
  }

  // Retrieve all and order

  def retrieveAllOrdered(optionalOrder: Option[String] = Some("asc")): Future[List[M]] =
    retrieveAllByFieldsOrdered(Some(Map()), optionalOrder)

  // Retrieve all and map to a specific field

  def retrieveAllByFieldsToField(optionalFieldsMap: Option[Map[String, String]],
                                 toField: M => String): Future[List[String]] =
    retrieveAllByFields(optionalFieldsMap).map(_.map(toField))

  def retrieveAllByFieldToField(fieldKey: String, fieldValue: String,
                                toField: M => String): Future[List[String]] =
    retrieveAllByField(fieldKey, fieldValue).map(_.map(toField))

  // retrieve for all field/id values

  def retrieveAllForAllFieldValues(fieldKey: String, fieldValues: List[String]): Future[List[M]] =
    Future.flatListSequence(fieldValues.map(retrieveAllByField(fieldKey, _)))

  def retrieveAllForAllIdValues(idValues: List[String]): Future[List[M]] =
    Future.flatListSequence(idValues.map(retrieveAllByField("ID", _)))

  // Retrieve one

  def retrieveByFieldToField(fieldKey: String, fieldValue: String,
                             toField: M => String): Future[Option[String]] =
    retrieveByField(fieldKey, fieldValue).map(_.map(toField))

  def crossJoin[N: Crudable, TN <: RelationalProfile#API#Table[N] : TableModel]
  (tableQueryN: TableQuery[TN]): Future[List[(M, N)]] = {
    val msg = s"crossJoin"
    logger.debug(msg)

    val joinedQuery = (query join tableQueryN).result
    logger.debug("\u001B[31mGenerated SQL statements: " + joinedQuery.statements + "\u001B[30m")
    db.run(joinedQuery).map(_.toList)
  }


  // helpers

  private def flatMapToOption(message: String): Future[List[M]] => Future[Option[M]] =
    futureList =>
      futureList.flatMap {
        case Nil =>
          val msg = s"$message ko"
          logger.debug(msg)
          Future(None)
        case r_m :: _ =>
          val msg = s"$message ok: $r_m"
          logger.debug(msg)
          Future(Some(r_m))
      }


  protected def mkRetrieveAllByFieldsQuery(optionalFieldsMap: Option[Map[String, String]]): SqlStreamingAction[Vector[M], M, Effect] = {
    import tableModel._
    import model._

    optionalFieldsMap match {
      case None =>
        sql"SELECT * FROM #$name".as[M]
      case Some(fieldsMap) if fieldsMap.isEmpty =>
        sql"SELECT * FROM #$name".as[M]
      case Some(fieldsMap) =>
        val filters = fieldsMapToFilters(fieldsMap)
        sql"SELECT * FROM #$name WHERE #$filters".as[M]
    }
  }

  protected def mkSearchAllByFieldsQuery(fieldsMap: Map[String, String]): SqlStreamingAction[Vector[M], M, Effect] = {
    import tableModel._
    import model._

    val filters = fieldsMapToSearchFilters(fieldsMap)
    sql"SELECT * FROM #$name WHERE #$filters".as[M]
  }

  protected def mkSearchMultipleColumns(str: String, columns: List[String]): SqlStreamingAction[Vector[M], M, Effect] = {
    import tableModel._
    import model._

    val filters = createLikeOrFilter(str, columns)
    sql"SELECT * FROM #$name WHERE #$filters".as[M]

  }

  protected def mkSearchMultipleColumsAndIds(ids: List[String],str: String, columns: List[String]) = {
    import model._
    import tableModel._
    val filters = createLikeOrFilter(str, columns)
    val idString = if (ids.nonEmpty) "AND ID IN ('"+ids.mkString("','")+"')" else ""
    sql"SELECT * FROM #$name WHERE #$filters #$idString".as[M]
  }

  protected def fieldsMapToFilters(fieldsMap: Map[String, String]): String = {
    val size = fieldsMap.size
    fieldsMap.zipWithIndex.foldLeft("") {
      (s, indexedFieldEntry) => indexedFieldEntry match {
        case ((fieldKey, fieldValue), index) if index == size - 1 =>
          s + s"$fieldKey='$fieldValue'"
        case ((fieldKey, fieldValue), _) =>
          s + s"$fieldKey='$fieldValue' AND "
      }
    }
  }
  protected def fieldsMapToSearchFilters(fieldsMap: Map[String, String]): String = {
    val size = fieldsMap.size
    fieldsMap.zipWithIndex.foldLeft("") {
      (s, indexedFieldEntry) => indexedFieldEntry match {
        case ((fieldKey, fieldValue), index) if index == size - 1 =>
          s + s"$fieldKey LIKE '%$fieldValue%'"
        case ((fieldKey, fieldValue), _) =>
          s + s"$fieldKey LIKE '%$fieldValue%' AND "
      }
    }
  }

  protected def createLikeOrFilter(subStr: String, fieldNames: List[String]): String = {
    val size = fieldNames.size
    fieldNames.zipWithIndex.foldLeft("") {
      (s, indexedFieldEntry) => indexedFieldEntry match {
        case (key, index) if index == size - 1 =>
          s + s"$key LIKE '%$subStr%'"
        case (key, _) =>
          s + s"$key LIKE '%$subStr%' OR "
      }
    }
  }


}