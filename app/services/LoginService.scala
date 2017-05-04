package services

import javax.inject.Inject

import models.{Credentials, Role, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json
import repositories.mongo.{RoleRepository, UserRepository}
import utils.WebTokenUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginService @Inject()(userRepository: UserRepository, rolesRepository: RoleRepository) {
  def validateLogin(credentials: Credentials): Future[Option[String]] = {
    getUser(credentials.username).flatMap {
      case Some(user) => validatePwdAndCreateJwt(credentials, user)
      case _ => Future(None)
    }
  }

  private def validatePwdAndCreateJwt(credentials: Credentials, user: User) = if (BCrypt.checkpw(credentials.password, user.passwordHash)) {
    getRolesOfUserForJwt(user)
  } else {
    Future(None)
  }

  private def getRolesOfUserForJwt(user: User) = {
    getRoles(user).map { roles =>
      roles.map(role => createJWT(user.username, user.id, role))
    }
  }

  def createUser(user: User) = userRepository.create(user.copy(passwordHash = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt)))


  def getUser(username: String): Future[Option[User]] = userRepository.retrieveByFields(Json.obj("username" -> Json.obj("$regex" -> ("^" + username), "$options" -> "-i")))

  def getRoles(user: User): Future[Option[Role]] = rolesRepository.retrieveById(user.roleId)

}
