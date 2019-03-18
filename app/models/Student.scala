package models

case class Student(
  id: Option[Long] = None,
  name: String = "",
  lastName: String = "",
  patronymic: String = "",
  groupId: Option[Long] = None,
  departmentId: Option[Long] = None
)
