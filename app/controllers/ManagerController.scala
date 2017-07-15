package controllers

import play.api.mvc._

class ManagerController extends Controller{

  def showManager = Action(Ok(views.html.index()).as("text/html"))

  def showScorer  = Action(Ok(views.html.scorer()).as("text/html"))

}
