package controllers

import play.api.mvc._

class ManagerController extends Controller{

  def showMangager = Action(Ok(views.html.index()).as("text/html"))

}
