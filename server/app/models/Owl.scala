package models

import db.DbCtx

case class Owl(impl: String, displayName: String, description: String,
               applicableStages: Option[Seq[Student.Stage]], stagesActive: Int,
               isImmediate: Boolean, id: Long = -1)

case class OwlsStudent(studentId: Long, owlId: Long, owlCount: Int, activeStagesLeft: Option[Int])

case class OwlPreloaded(id: Long, name: String, description: String, applicableStages: Option[Seq[Student.Stage]],
                        owlCount: Int, isActive: Boolean)

class OwlDao(db: DbCtx) {
  import db._

  def load(studentId: Long): Seq[OwlPreloaded] =
    run(
      query[OwlsStudent]
        .filter(_.studentId == lift(studentId))
        .join(query[Owl]).on {
          case (os, o) => os.owlId == o.id
        }
        .map {
          case (os, o) => OwlPreloaded(o.id, o.displayName, o.description, o.applicableStages,
            os.owlCount, os.activeStagesLeft.isDefined)
        }
    )

  def findAvailable(studentId: Long, owlId: Long): Option[Owl] =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.owlId == lift(owlId) && os.owlCount > 0 && os.activeStagesLeft.isEmpty)
        .join(query[Owl]).on {
          case (os, o) => os.owlId == o.id
        }
        .map(_._2)
    ).headOption

  def applyNonImmediate(studentId: Long, owl: Owl): Unit =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.owlId == lift(owl.id))
        .update(os => os.owlCount -> (os.owlCount - 1),
          _.activeStagesLeft -> lift(Some(owl.stagesActive): Option[Int]))
    )

  def applyImmediate[T](studentId: Long, owlId: Long)(apply: => T): T =
    transaction {
      val applicationResult = apply
      run(
        query[OwlsStudent]
          .filter(os => os.studentId == lift(studentId) && os.owlId == lift(owlId))
          .update(os => os.owlCount -> (os.owlCount - 1))
      )
      applicationResult
    }

  def loadForStageUpdate(studentId: Long, stage: Student.Stage): Seq[(Long, String)] =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.activeStagesLeft.isDefined)
        .join(query[Owl]).on {
          case (os, o) => os.owlId == o.id && o.applicableStages.exists(_.contains(lift(stage)))
        }
        .map {
          case (os, o) => (o.id, o.impl)
        }
    )

  def updatePostStageUpdate(studentId: Long, owlIds: Seq[Long]): Unit =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && lift(owlIds).contains(os.owlId))
        .update(os => os.activeStagesLeft -> os.activeStagesLeft.flatMap(s => if (s > 1) Some(s - 1) else None))
    )
}
