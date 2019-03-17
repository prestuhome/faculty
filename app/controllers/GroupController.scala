package controllers

import javax.inject.Inject
import models.Group
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, optional}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.GroupService
import views.html.groups._

import scala.concurrent.{ExecutionContext, Future}

class GroupController @Inject()(
  cc: ControllerComponents,
  groupService: GroupService
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
    val groupOpt = groupService.findById(id)

    groupOpt.map {
      case Some(group) => Ok(editGroupForm(id, form.fill(group)))
      case None => NotFound
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(editGroupForm(id, formWithErrors))),
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

  def save() = Action.async { implicit rs =>
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
