package models

import db.DbCtx

case class CreatureFight(studentId: Long, creatureId: Long, creatureHp: Int)

class FightDao(val db: DbCtx) {
  import db._

  def find(student: Student): CreatureFight =
    run(query[CreatureFight].filter(_.studentId == lift(student.id))).head

  def create(fight: CreatureFight): Unit =
    run(query[CreatureFight].insert(lift(fight)))
}
