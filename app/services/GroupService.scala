package services

import javax.inject.Inject
import models.Group
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GroupService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.profile.api._
  private val groups = TableQuery[Groups]

  private def _findById(id: Long): DBIO[Option[Group]] =
    groups.filter(_.id === id).result.headOption

  private def _findByCode(code: String): Query[Groups, Group, List] =
    groups.filter(_.code === code).to[List]

  def findById(id: Long): Future[Option[Group]] =
    db.run(_findById(id))

  def findByCode(code: String): Future[List[Group]] =
    db.run(_findByCode(code).result)

  def all: Future[List[Group]] =
    db.run(groups.to[List].result)

  def insert(group: Group): Future[Unit] =
    db.run(groups += group).map(_ => ())

  def update(id: Long, group: Group): Future[Unit] = {
    val groupToUpdate: Group = group.copy(Some(id))
    db.run(groups.filter(_.id === id).update(groupToUpdate)).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(groups.filter(_.id === id).delete).map(_ => ())

  private class Groups(tag: Tag) extends Table[Group](tag, "groups") {

    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def code = column[String]("code")

    def * = (id.?, code) <> (Group.tupled, Group.unapply)

  }

}
