package controllers

import play.api.mvc.{AnyContent, InjectedController, Request}

class OptionsController extends InjectedController {


  val methods = List("OPTIONS")
  def options(url: String) = Action {
      NoContent.withHeaders("Access-Control-Allow-Origin" -> "*", "Access-Control-Allow-Headers" -> "*")
  }

  def rootOptions = options("/")
}
