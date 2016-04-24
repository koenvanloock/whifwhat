package models

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 20:01
  */
trait Crudable[T] {
  def getId: String
  def setId(newId: String): T
}
