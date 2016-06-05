package services

import javax.inject.Inject

import models.{Role, User, Credentials}
import org.mindrot.jbcrypt.BCrypt
import repositories.{RoleRepository, UserRepository}
import utils.WebTokenUtils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginService @Inject()(userRepository: UserRepository, rolesRepository: RoleRepository) extends GenericAtomicCrudService[User]{
  def validateLogin(credentials: Credentials): Future[Option[String]] = {
    getUser(credentials.username).flatMap{
      case Some(user) =>
        BCrypt.checkpw(credentials.password, user.paswordHash) match {
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


  def getUser(username: String): Future[Option[User]] = userRepository.retrieveByField("username", username)
  def getRoles(user: User): Future[Option[Role]] = rolesRepository.retrieveById(user.roleId)

}
