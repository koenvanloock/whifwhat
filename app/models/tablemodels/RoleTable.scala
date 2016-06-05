package models.tablemodels

import models.Role

import scala.language.postfixOps
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

class RolesTable(tag: Tag) extends Table[Role](tag, "ROLES"){
  def roleId = column[String]("ID", O.PrimaryKey, O.Length(100))
  def roleName = column[String]("ROLE_NAME")

  def * = (roleId, roleName) <> (Role.tupled, Role.unapply)
}

object RoleTableTableModel {

  implicit object userTableTableModel extends TableModel[RolesTable] {
    override def getRepId(tm: RolesTable): Rep[String] = tm.roleId
  }

}