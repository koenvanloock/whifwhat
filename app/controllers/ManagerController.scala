package controllers

import play.api.mvc._

class ManagerController extends InjectedController{

  def showManager = Action(Ok(views.html.index()).as("text/html"))

  def showScorer  = Action(Ok(views.html.scorer()).as("text/html"))

}
