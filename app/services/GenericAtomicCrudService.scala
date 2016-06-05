package services

import java.util.UUID

import models.Crudable

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


abstract class GenericAtomicCrudService[T <: Crudable[T]] {

  def create(crudable:  T) = {
    val newId = UUID.randomUUID().toString
    //create in db
    Future(Some(crudable))
  }

  def update(crudable: T): Future[Option[T]] = {
    // update in db
    Future(Some(crudable))
  }

  def delete(id: String) = {
    // delete in db
    Future(Some(s"id $id deleted"))
  }
}
