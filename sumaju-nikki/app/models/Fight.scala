package models

import db.DbCtx

case class Fight(studentId: Long, creatureId: Long, creatureHp: Int)

class FightDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Fight]("fights"))

  def find(student: Student): Fight =
    run(schema.filter(_.studentId == lift(student.id))).head

  def create(fight: Fight): Unit =
    run(schema.insert(lift(fight)))
}
