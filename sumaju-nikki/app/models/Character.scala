package models

import java.time.LocalDateTime

import db.DbCtx

case class Character(stage: String, nextStageTime: LocalDateTime,
  academic_year: Int)

class CharacterDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Character]("characters"))

  def findPendingTurnUpdates(count: Int): Seq[Character] =
    run(
      schema
        .filter(_.nextStageTime <= lift(LocalDateTime.now()))
        .sortBy(_.nextStageTime)(Ord.asc)
        .take(lift(count))
        .forUpdate
    )
}
