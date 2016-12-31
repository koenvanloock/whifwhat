package services

import javax.inject.Inject

import models.{Credentials, Role, User}
import org.mindrot.jbcrypt.BCrypt
import repositories.mongo.{RoleRepository, UserRepository}
import utils.WebTokenUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginService @Inject()(userRepository: UserRepository, rolesRepository: RoleRepository){
  def validateLogin(credentials: Credentials): Future[Option[String]] = {
    getUser(credentials.username).flatMap{
      case Some(user) =>
        println(user.passwordHash)
        BCrypt.checkpw(credentials.password, user.passwordHash) match {
          case true =>
            getRoles(user).map { roles =>
              roles.map(role => createJWT(user.username, role))
            }
          case _ =>
            Future(None)
        }
      case _ => Future(None)
    }
  }

  def createUser(user: User) = userRepository.create(user.copy(passwordHash = BCrypt.hashpw(user.passwordHash, BCrypt.gensalt)))


  def getUser(username: String): Future[Option[User]] = userRepository.retrieveByField("username", username)
  def getRoles(user: User): Future[Option[Role]] = rolesRepository.retrieveById(user.roleId)

}
