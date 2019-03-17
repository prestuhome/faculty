package services

import javax.inject.Inject
import models.Department
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DepartmentService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private val departments = TableQuery[Departments]

  private def _findById(id: Long): DBIO[Option[Department]] =
    departments.filter(_.id === id).result.headOption

  private def _findByName(name: String): Query[Departments, Department, List] =
    departments.filter(_.name === name).to[List]

  def findById(id: Long): Future[Option[Department]] =
    db.run(_findById(id))

  def findByName(name: String): Future[List[Department]] =
    db.run(_findByName(name).result)

  def all: Future[List[Department]] =
    db.run(departments.to[List].result)

  def insert(department: Department): Future[Unit] =
    db.run(departments += department).map(_ => ())

  def insert(departments: Seq[Department]): Future[Unit] =
    db.run(this.departments ++= departments).map(_ => ())

  def update(id: Long, department: Department): Future[Unit] = {
    val departmentToUpdate: Department = department.copy(Some(id))
    db.run(departments.filter(_.id === id).update(departmentToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(departments.filter(_.id === id).delete).map(_ => ())

  private class Departments(tag: Tag) extends Table[Department](tag, "departments") {

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def name = column[String]("name")

    def * = (id.?, name) <> (Department.tupled, Department.unapply)

  }

}
