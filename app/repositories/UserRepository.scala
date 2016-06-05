package repositories

import javax.inject.Inject

import models.{UserResultModel, User}
import models.tablemodels.UserTable
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.lifted.TableQuery
import UserResultModel._
import models.tablemodels.UserTableTableModel._

class UserRepository @Inject()(@NamedDatabase("user") override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[User, UserTable](dbConfigProvider = dbConfigProvider)("USERS"){
  override def query: driver.api.TableQuery[UserTable] = TableQuery[UserTable]
}
