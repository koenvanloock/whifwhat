package repositories.mongo

import com.typesafe.config.Config
import models.Model
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json.{JsValueWrapper, toJsFieldJsValueWrapper}
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.MongoDriver
import reactivemongo.play.json._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.collection.JSONCollection
import utils.JsonUtils._
import utils.PaginationUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class GenericMongoRepo[M: Model]
(name: String, defaultOrder: String, defaultPageSize: Int)
(implicit reactiveMongoApi: ReactiveMongoApi) {

  private final val logger: Logger = LoggerFactory.getLogger(classOf[GenericMongoRepo[M]])

  val model = implicitly[Model[M]]

  def collectionFuture = reactiveMongoApi.database.map(_.collection[JSONCollection](name))


  def generateId() = BSONObjectID.generate.stringify

  //
  // standard CRUD part
  //

  // Create

  def create(m: M): Future[M] = {
    val msg = s"create($m)"
    logger.debug(msg)
    insert(beforeInsert(m))
  }

  // Retrieve

  def retrieveById(id: String): Future[Option[M]] = {
    retrieveAllByField("id",id).map(_.headOption)
  }

  def retrieveAll(): Future[List[M]] = collectionFuture.flatMap(collection => collection.find(Json.obj()).cursor[M]().collect[List]())


  def retrieveAllByFields(optionalFieldsMap: Option[Map[String, String]]): Future[List[M]] = {
    val msg = s"retrieveAllByFields( $optionalFieldsMap )"
    logger.debug(msg)
    val filters = fieldsMapToFilters(optionalFieldsMap.getOrElse(Map()))

    collectionFuture.flatMap(collection =>
      collection
      .find(Json.obj(filters: _*))
      .cursor[M]()
      .collect[List]())
  }

  def retrieveByField(fieldKey: String, fieldValue: String) = {
    retrieveAllByField(fieldKey, fieldValue).map(_.headOption)
  }

  def retrieveAllByField(fieldKey: String, fieldValue: String): Future[List[M]] =
    retrieveAllByFields(Some(Map(fieldKey -> fieldValue)))

  // Update

  def update(u_m: M): Future[M] = {
    val msg = s"update($u_m)"
    println(msg)
    model.getId(u_m).map(checked(uncheckedUpdate(u_m))).getOrElse {
      val msg = s"update with object $u_m ko (no id)"
      logger.debug(msg)
      Future.fromTry(Failure(new Exception(msg)))
    }
  }

  // Delete

  def delete(id: String): Future[Unit] = {
    val msg = s"delete($id)"
    logger.debug(msg)
    checked(uncheckedDelete)(id)
  }

  //
  // extra stuff
  //

  def retrieveAllOrdered(optionalOrder: Option[String]): Future[List[M]] = {
    val msg = s"retrieveAllOrdered($optionalOrder)"
    logger.debug(msg)
    collectionFuture.flatMap(collection =>
      collection.find(Json.obj())
      .sort(using(Some(optionalOrder.getOrElse(defaultOrder))))
      .cursor[M]()
      .collect[List]())
  }

  def retrieveAllOrderedAndPaginated(optionalOrder: Option[String], optionalPageNumberString: Option[String], optionalPageSizeString: Option[String]): Future[List[M]] =
    retrieveAllOrdered(optionalOrder)
      .map(paginate(optionalPageNumberString, optionalPageSizeString))

  def retrieveAllByFieldsOrdered(optionalFieldsMap: Option[Map[String, String]], optionalOrder: Option[String]): Future[List[M]] = {
    val msg = s"retrieveAllByFieldsOrdered($optionalFieldsMap, $optionalOrder)"
    logger.debug(msg)
    val filters = fieldsMapToFilters(optionalFieldsMap.getOrElse(Map()))
    collectionFuture.flatMap(collection => collection
      .find(Json.obj(filters: _*))
      .sort(using(Some(optionalOrder.getOrElse(defaultOrder))))
      .cursor[M]()
      .collect[List]())
  }

  def retrieveAllByFieldsOrderedAndPaginated(optionalFieldsMap: Option[Map[String, String]], optionalOrder: Option[String], optionalPageNumberString: Option[String], optionalPageSizeString: Option[String]): Future[List[M]] =
    retrieveAllByFieldsOrdered(optionalFieldsMap, optionalOrder)
      .map(paginate(optionalPageNumberString, optionalPageSizeString))

  //
  // helpers
  //

  private def fieldsMapToFilters(fieldsMap: Map[String, String]): Seq[(String, JsValueWrapper)] =
    fieldsMap.mapValues(toJsFieldJsValueWrapper[String]).toSeq

  private def checked[Z](action: String => Future[Z])(id: String): Future[Z] =
    flatMapToOption("retrieve by id")(retrieveAllByField("id", id)).flatMap { r_m =>
      action(id)
    }

  protected def beforeInsert(m: M): M =
    model.setId(generateId())(m)

  protected def insert(i_m: M): Future[M] = {
    collectionFuture.flatMap(collection => collection
      .insert(i_m)
      .map { writeResult =>
        val msg = s"create object $i_m ok"
        logger.debug(msg)
        i_m
      })
  }

  private def uncheckedUpdate(u_m: M)(id: String): Future[M] =
    collectionFuture.flatMap(collection => collection
      .update(Json.obj("id" -> id), u_m)
      .map { writeResult =>
        val msg = s"update with object $u_m ok"
        logger.debug(msg)
        u_m
      })

  private def uncheckedDelete(id: String): Future[Unit] =
    collectionFuture.flatMap(collection =>
      collection
        .remove(Json.obj("id" -> id))
      .map { writeResult =>
        val msg = s"delete with id $id ok"
        logger.debug(msg)
      })

  private def flatMapToOption(message: String): Future[List[M]] => Future[Option[M]] =
    futureList =>
      futureList.flatMap {
        case Nil =>
          val msg = s"message ko"
          logger.debug(msg)
          Future(None)
        case r_m :: _ =>
          val msg = s"message ok: $r_m"
          logger.debug(msg)
          Future(Some(r_m))
      }

}
