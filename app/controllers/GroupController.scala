package controllers

import javax.inject.Inject
import models.Group
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, optional}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{GroupService, StudentService}
import views.html.groups._

import scala.concurrent.{ExecutionContext, Future}

class GroupController @Inject()(
  cc: ControllerComponents,
  groupService: GroupService,
  studentService: StudentService
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
    with I18nSupport {

  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "code" -> nonEmptyText
    )(Group.apply)(Group.unapply)
  )

  def edit(id: Long) = Action.async { implicit request =>
    val groupAndStudents = for {
      group <- groupService.findById(id)
      students <- studentService.findByGroupId(id)
    } yield (group, students)

    groupAndStudents.map {
      case (group, students) =>
        group match {
          case Some(g) => Ok(editGroupForm(id, form.fill(g), students))
          case None => NotFound
        }
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => studentService.findByGroupId(id).map(students => BadRequest(editGroupForm(id, formWithErrors, students))),
      group => {
        for {
          _ <- groupService.update(id, group)
        } yield Ok.flashing("success" -> s"Group with id $id has been updated")
      }
    )
  }

  def create() = Action.async { implicit request =>
    Future.successful(Ok(createGroupForm(form)))
  }

  def save() = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(createGroupForm(formWithErrors))),
      group => {
        for {
          _ <- groupService.insert(group)
        } yield Ok.flashing("success" -> s"Group has been created")
      }
    )
  }

  def delete(id: Long) = Action.async {
    for {
      _ <- groupService.delete(id)
    } yield Ok.flashing("success" -> s"Group with id $id was deleted")
  }

}
