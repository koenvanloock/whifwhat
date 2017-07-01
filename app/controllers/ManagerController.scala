package controllers

import play.api.mvc._

class ManagerController extends Controller{

  def showManager = Action(Ok(views.html.index()).as("text/html"))

}
