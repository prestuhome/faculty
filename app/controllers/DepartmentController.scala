package controllers

import javax.inject.Inject
import models.Department
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, optional}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.DepartmentService
import views.html.departments._

import scala.concurrent.{ExecutionContext, Future}

class DepartmentController @Inject()(
  cc: ControllerComponents,
  departmentService: DepartmentService
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
  with I18nSupport {

  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText
    )(Department.apply)(Department.unapply)
  )

  def edit(id: Long) = Action.async { implicit request =>
    val departmentOpt = departmentService.findById(id)

    departmentOpt.map {
      case Some(department) => Ok(editDepartmentForm(id, form.fill(department)))
      case None => NotFound
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(editDepartmentForm(id, formWithErrors))),
      department => {
        for {
          _ <- departmentService.update(id, department)
        } yield Ok.flashing("success" -> s"Department with id $id has been updated")
      }
    )
  }

  def create() = Action.async { implicit request =>
    Future.successful(Ok(createDepartmentForm(form)))
  }

  def save() = Action.async { implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(createDepartmentForm(formWithErrors))),
      department => {
        for {
          _ <- departmentService.insert(department)
        } yield Ok.flashing("success" -> s"Department has been created")
      }
    )
  }

  def delete(id: Long) = Action.async {
    for {
      _ <- departmentService.delete(id)
    } yield Ok.flashing("success" -> s"Department with id $id has been deleted")
  }

}
