package models

import slick.jdbc.GetResult
import utils.RankConverter

case class Role(roleId: String, roleName: String) extends Crudable[Role]{
  override def getId(crudable: Role): String = crudable.roleId


  override implicit val getResult: GetResult[Role] = GetResult(r => Role(r.<<,r.<<)  )
}

case class User(userId: String, username: String, paswordHash: String, roleId: String) extends Crudable[User]{
  override def getId(user: User): String = user.userId

  override implicit val getResult: GetResult[User] = GetResult(r => User(r.<<,r.<<, r.<<,r.<<))
}

object RoleResultModel{
  implicit object RoleIsCrudable extends Crudable[Role]{
    override def getId(crudable: Role): String = crudable.roleId

    override implicit val getResult: GetResult[Role] = GetResult(r => Role(r.<<,r.<<))
  }
}

object UserResultModel{
  implicit object RoleIsCrudable extends Crudable[User]{
    override def getId(crudable: User): String = crudable.roleId

    override implicit val getResult: GetResult[User] = GetResult(r => User(r.<<,r.<<,r.<<,r.<<))
  }
}