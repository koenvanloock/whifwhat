package models

import slick.jdbc.GetResult

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 20:01
  */
trait Crudable[T] {
  def getId(crudable: T): String
  implicit val getResult : GetResult[T]
}
