package repositories

import javax.inject.Inject

import models.Role
import models.tablemodels.RolesTable
import models.RoleResultModel._
import models.tablemodels.RoleTableTableModel._
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.lifted.TableQuery

class RoleRepository @Inject()(@NamedDatabase("user") override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[Role, RolesTable](dbConfigProvider = dbConfigProvider)("ROLES"){
  override def query: driver.api.TableQuery[RolesTable] = TableQuery[RolesTable]
}