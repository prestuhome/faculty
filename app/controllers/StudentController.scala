package controllers

import javax.inject.Inject
import models.Student
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, optional}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.StudentService
import views.html.students._

import scala.concurrent.{ExecutionContext, Future}

class StudentController @Inject()(
  cc: ControllerComponents,
  studentService: StudentService
)(implicit exec: ExecutionContext)
  extends AbstractController(cc)
    with I18nSupport {

  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "patronymic" -> nonEmptyText,
      "groupId" -> optional(longNumber),
      "departmentId" -> optional(longNumber)
    )(Student.apply)(Student.unapply)
  )

  def edit(id: Long) = Action.async { implicit request =>
    val studentOpt = studentService.findById(id)

    studentOpt.map {
      case Some(student) => Ok(editStudentForm(id, student.groupId.get, form.fill(student)))
      case None => NotFound
    }
  }

  def update(id: Long) = Action.async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(editStudentForm(id, formWithErrors.value.get.groupId.get, formWithErrors))),
      student => {
        for {
          _ <- studentService.update(id, student)
        } yield Ok.flashing("success" -> s"Student with id $id has been updated")
      }
    )
  }

  def create(groupId: Long) = Action.async { implicit request =>
    Future.successful(Ok(createStudentForm(groupId, form)))
  }

  def save() = Action.async { implicit rs =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(createStudentForm(formWithErrors.value.get.groupId.get, formWithErrors))),
      student => {
        for {
          _ <- studentService.insert(student)
        } yield Ok.flashing("success" -> s"Student has been created")
      }
    )
  }

  def listStudentsToChoose(departmentId: Long) = Action.async { implicit request =>
    studentService.allStudentsWithoutDepartment().map(students => Ok(listStudentsToChooseView(departmentId, students)))
  }

  def setStudentDepartment(studentId: Long, departmentId: Long) = Action.async { implicit request =>
    for {
      _ <- studentService.setStudentDepartment(studentId, departmentId)
    } yield Ok
  }

  def removeStudentFromDepartment(id: Long) = Action.async {
    for {
      _ <- studentService.removeStudentFromDepartment(id)
    } yield Ok
  }

  def delete(id: Long) = Action.async {
    for {
      _ <- studentService.delete(id)
    } yield Ok.flashing("success" -> s"Student with id $id has been deleted")
  }

}
