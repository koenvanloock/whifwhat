package models.tablemodels

import models.User
import play.api.libs.functional.syntax._
import play.api.libs.json._
import slick.jdbc.GetResult
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.TableQuery
import scala.language.postfixOps

class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[String]("ID", O.PrimaryKey, O.Length(255))

  def username = column[String]("USERNAME", O.Length(255))

  def passwordHash = column[String]("PASSWORD_HASH", O.Length(255))
  def roleId = column[String]("roleId")


  def * = (id, username, passwordHash, roleId) <>(User.tupled, User.unapply)
}

object UserTableTableModel {

  implicit object userTableTableModel extends TableModel[UserTable] {
    override def getRepId(tm: UserTable): Rep[String] = tm.id
  }

}
