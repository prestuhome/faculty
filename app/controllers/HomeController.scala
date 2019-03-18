package controllers

import javax.inject.Inject
import play.api.mvc._
import services.{DepartmentService, GroupService}

import scala.concurrent.ExecutionContext

class HomeController @Inject()(
  cc: ControllerComponents,
  departmentService: DepartmentService,
  groupService: GroupService
)(implicit exec: ExecutionContext)
  extends AbstractController(cc) {

  def home = Action.async {
    departmentService.all zip groupService.all map { depsAndGroups => {
      Ok(views.html.home(depsAndGroups._1, depsAndGroups._2))
    }}
  }

}
