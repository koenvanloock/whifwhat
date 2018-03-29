package controllers

import javax.inject.Inject

import models.{Credentials, User}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, InjectedController}
import services.LoginService
import utils.JsonUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginController @Inject()(loginService: LoginService) extends InjectedController{
  val credentialsFormat = Json.format[Credentials]
  implicit val userFormat = Json.format[User]


  def login = Action.async(parse.tolerantJson) { request =>
    JsonUtils.parseRequestBody(request)(credentialsFormat) match {
      case Some(credentials) =>
        loginService.validateLogin(credentials).map {
          case Some(token) => Ok(Json.toJson(token))
          case _ => BadRequest
        }
      case _ => Future(BadRequest)
    }
  }

  def createUser = Action.async(parse.tolerantJson){ request =>
    JsonUtils.parseRequestBody(request)(userFormat).map{ user =>
        loginService.createUser(user).map( user => Ok(Json.toJson(user)))
    }.getOrElse(Future(BadRequest("Geef een geldige user op")))
  }


}
