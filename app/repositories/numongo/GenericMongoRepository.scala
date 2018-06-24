package repositories.numongo

import play.api.libs.json.Json.{JsValueWrapper, toJsFieldJsValueWrapper}
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GenericMongoRepository[M <: Model[ID], ID](name: String, modelReads: Reads[M], modelWrites: OWrites[M])
                                          (implicit reactiveMongoApi: ReactiveMongoApi) {

  implicit val idWrites: Writes[ID] = {
    case s: String => JsString(s)
    case n: Int => JsNumber(n)
    case n: Long => JsNumber(n)
    case b: BSONObjectID => b.asInstanceOf[JsObject]
    case a: Any => JsString(a.toString)
  }
  implicit val reads: Reads[M] = modelReads
  implicit val writes: OWrites[M] = modelWrites

  def collectionFuture: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection](name))

  val ID_FIELD = "_id"
  val DFEFAULT_PAGE_SIZE = 100
  val DEFAULT_PAGING = Pagination(1, DFEFAULT_PAGE_SIZE)

  def generateId(): String = BSONObjectID.generate.stringify

  def create(m: M): Future[M] = collectionFuture.flatMap(collection =>
    collection.update(Json.obj(ID_FIELD -> Json.toJson(m.id)), m, upsert = true).map(_ => m))

  def findById(id: String): Future[Option[M]] = findFirstByField(ID_FIELD, id)

  def findAll(): Future[List[M]] = collectionFuture
    .flatMap(collection => collection
      .find(Json.obj())
      .cursor[M]().collect[List](DFEFAULT_PAGE_SIZE, Cursor.FailOnError[List[M]]()))


  def findAll(pageNumber: Int, pageSize: Int): Future[Page[M]] = {
    collectionFuture
      .flatMap { collection =>
        collection
          .find(Json.obj())
          .skip((pageNumber - 1) * pageSize)
          .cursor[M]().collect[List](pageSize, Cursor.FailOnError[List[M]]())
      }
      .flatMap(list => collectionFuture.flatMap(_.count())
        .map(Page(pageNumber, pageSize, _, list)))

  }

  def findFirstByJsQuery(jsonObject: JsObject): Future[Option[M]] = collectionFuture.flatMap(collection =>
    collection
      .find(jsonObject)
      .cursor[M](ReadPreference.primary)
      .collect[List](1, Cursor.FailOnError[List[M]]()))
    .map(_.headOption)


  def findAllByJsQuery(jsonObject: JsObject): Future[List[M]] = collectionFuture.flatMap(collection =>
    collection
      .find(jsonObject)
      .cursor[M]()
      .collect[List](DFEFAULT_PAGE_SIZE, Cursor.FailOnError[List[M]]()))

  def findAllByJsQuery(jsonObject: JsObject, pagination: Pagination): Future[Page[M]] = collectionFuture.flatMap(collection =>
    collection
      .find(jsonObject)
      .skip((pagination.pageNumber - 1) * pagination.pageSize)
      .cursor[M]()
      .collect[List](DFEFAULT_PAGE_SIZE, Cursor.FailOnError[List[M]]()))
    .flatMap(data => collectionFuture
      .flatMap(_.count(Some(jsonObject)))
      .map(count => Page(pagination.pageNumber, pagination.pageSize, count, data)))

  def findFirstByField(fieldKey: String, fieldValue: JsValueWrapper): Future[Option[M]] = {
    findAllByField(fieldKey, fieldValue).map(_.headOption)
  }

  def findAllByField(fieldKey: String, fieldValue: JsValueWrapper): Future[List[M]] =
    findAllByJsQuery(Json.obj(fieldKey -> fieldValue))

  def findAllByField(fieldKey: String, fieldValue: JsValueWrapper, page: Pagination): Future[Page[M]] =
    findAllByJsQuery(Json.obj(fieldKey -> fieldValue), page)

  def update(modelObject: M): Future[M] = checkIfIdPresent(uncheckedUpdate(modelObject))(modelObject.id)

  def delete(id: ID): Future[Option[M]] = {
    checkIfIdPresent(uncheckedDelete)(id)
  }

  def deleteAll(): Future[Boolean] = collectionFuture.flatMap(collection => collection.drop(failIfNotFound = false))

  def findAllOrdered(order: String): Future[List[M]] = {
    collectionFuture.flatMap(collection =>
      collection.find(Json.obj())
        .sort(JsonUtils.using(order))
        .cursor[M]()
        .collect[List](DFEFAULT_PAGE_SIZE, Cursor.FailOnError[List[M]]()))
  }

  def findAllOrderedAndPaginated(order: String, page: Pagination = DEFAULT_PAGING): Future[Page[M]] =
    collectionFuture.flatMap(collection =>
      collection.find(Json.obj())
        .skip(page.skip)
        .sort(JsonUtils.using(order))
        .cursor[M]()
        .collect[List](page.pageSize, Cursor.FailOnError[List[M]]()))
      .flatMap(list => collectionFuture.flatMap(_.count())
        .map(Page(page.pageNumber, page.pageSize, _, list)))

  def retrieveAllByFieldsOrdered(optionalFieldsMap: Option[Map[String, String]], order: String): Future[List[M]] = {
    val filters = fieldsMapToFilters(optionalFieldsMap.getOrElse(Map()))
    collectionFuture.flatMap(collection => collection
      .find(Json.obj(filters: _*))
      .sort(JsonUtils.using(order))
      .cursor[M]()
      .collect[List](DFEFAULT_PAGE_SIZE, Cursor.FailOnError[List[M]]()))
  }

  def retrieveAllByFieldsOrderedAndPaginated(optionalFieldsMap: Option[Map[String, String]], order: String, page: Pagination = DEFAULT_PAGING): Future[Page[M]] = {
    val filters = fieldsMapToFilters(optionalFieldsMap.getOrElse(Map()))
    collectionFuture.flatMap(collection => collection
      .find(Json.obj(filters: _*))
      .skip(page.skip)
      .sort(JsonUtils.using(order))
      .cursor[M]()
      .collect[List](page.pageSize, Cursor.FailOnError[List[M]]())
    ).flatMap(list => collectionFuture.flatMap(_.count(Some(Json.obj(filters: _*))))
      .map(Page(page.pageNumber, page.pageSize, _, list)))
  }

  private def fieldsMapToFilters(fieldsMap: Map[String, String]): Seq[(String, JsValueWrapper)] =
    fieldsMap.mapValues(toJsFieldJsValueWrapper[String]).toSeq

  private def checkIfIdPresent[Z](action: ID => Future[Z])(id: ID): Future[Z] =
    flatMapToOption(findAllByField(ID_FIELD, Json.toJson(id)))
      .flatMap(_ => action(id))

  protected def insert(insertedModel: M): Future[M] = {
    collectionFuture.flatMap(collection =>
      collection
        .insert(insertedModel)
        .map(_ => insertedModel))
  }

  private def uncheckedUpdate(modelObject: M)(id: ID): Future[M] =
    collectionFuture.flatMap(collection =>
      collection
        .update(Json.obj(ID_FIELD -> Json.toJson(id)), modelObject)
        .map(_ => modelObject))

  private def uncheckedDelete(id: ID): Future[Option[M]] = {
    val selector = Json.obj(ID_FIELD -> Json.toJson(id))
    collectionFuture
      .flatMap(_.findAndRemove(selector)
        .map(_.result[M]))
  }

  private def flatMapToOption: Future[List[M]] => Future[Option[M]] =
    futureList =>
      futureList.flatMap {
        case Nil => Future.successful(None)
        case firstElem :: _ => Future.successful(Some(firstElem))
      }
}
