package services

import javax.inject.Inject
import models.Student
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StudentService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private val students = TableQuery[Students]

  private def _findById(id: Long): DBIO[Option[Student]] =
    students.filter(_.id === id).result.headOption

  private def _findByGroupId(groupId: Long): Query[Students, Student, List] =
    students.filter(_.groupId === groupId).to[List]

  private def _findByDepartmentId(departmentId: Long): Query[Students, Student, List] =
    students.filter(_.departmentId === departmentId).to[List]

  private def _allStudentsWithoutDepartment(): Query[Students, Student, List] = {
    students.filterNot(_.departmentId.isDefined).to[List]
  }

  def findById(id: Long): Future[Option[Student]] =
    db.run(_findById(id))

  def findByGroupId(groupId: Long): Future[List[Student]] =
    db.run(_findByGroupId(groupId).result)

  def findByDepartmentId(departmentId: Long): Future[List[Student]] =
    db.run(_findByDepartmentId(departmentId).result)

  def all: Future[List[Student]] =
    db.run(students.to[List].result)

  def insert(student: Student): Future[Unit] =
    db.run(students += student).map(_ => ())

  def update(id: Long, student: Student): Future[Unit] = {
    val studentToUpdate: Student = student.copy(Some(id))
    db.run(students.filter(_.id === id).update(studentToUpdate)).map(_ => ())
  }

  def allStudentsWithoutDepartment(): Future[List[Student]] = {
    db.run(_allStudentsWithoutDepartment().result)
  }

  def setStudentDepartment(studentId: Long, departmentId: Long): Future[Unit] = {
    db.run(students.filter(_.id === studentId).map(_.departmentId).update(Some(departmentId))).map(_ => ())
  }

  def removeStudentFromDepartment(id: Long): Future[Unit] = {
    db.run(students.filter(_.id === id).map(_.departmentId).update(None)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(students.filter(_.id === id).delete).map(_ => ())

  private class Students(tag: Tag) extends Table[Student](tag, "students") {

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")
    def lastName = column[String]("last_name")
    def patronymic = column[String]("patronymic")
    def groupId = column[Option[Long]]("group_id")
    def departmentId = column[Option[Long]]("department_id")

    def * = (id.?, name, lastName, patronymic, groupId, departmentId) <> (Student.tupled, Student.unapply)

  }

}
