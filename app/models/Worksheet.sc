
import reactivemongo.api.{MongoConnection, MongoDriver}
import slick.driver

import scala.concurrent.Future
import scala.util.Try

val uri = "mongodb://localhost:27017/pipoka"
val driver = new MongoDriver

val database = for {
  uri <- Future.fromTry(MongoConnection.parseURI(uri))
  con = driver.connection(uri)
  dn <- Future(uri.db.get)
  db <- con.database(dn)
} yield db

database.onComplete {
  case resolution =>
    println(s"DB resolution: $resolution")
    driver.close()
}