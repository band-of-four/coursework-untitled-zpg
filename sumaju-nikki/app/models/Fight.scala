package models

import db.DbCtx

case class Fight(student: Student, creature: Creature, creature_hp: Int)

class FightDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Fight]("fights"))

  def findFightByStudent(student: Student): Fight =
    run(schema.filter(_.student.id == lift(student.id))).head

  def insert(fight: Fight): Unit =
    query[Fight].insert(fight)
