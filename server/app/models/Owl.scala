package models

import db.DbCtx

case class Owl(impl: String, displayName: String, description: String,
               applicableStage: Option[Student.Stage], stagesActive: Int, id: Long = -1)

case class OwlsStudent(studentId: Long, owlId: Long, owlCount: Int, activeStagesLeft: Option[Int])

case class OwlPreloaded(name: String, description: String, applicableStage: Option[Student.Stage],
                        owlCount: Int, isActive: Boolean)

case class OwlStageUpdate(impl: String, applicableStage: Option[Student.Stage], activeStagesLeft: Option[Int])

class OwlDao(db: DbCtx) {
  import db._

  def apply(studentId: Long, owlId: Long): Boolean =
    run(infix"owl_apply(${lift(studentId)}, ${lift(owlId)})".as[Boolean])

  def findById(owlId: Long): Owl =
    run(query[Owl].filter(_.id == lift(owlId))).head

  def load(studentId: Long): Seq[OwlPreloaded] =
    run(
      query[OwlsStudent]
        .filter(_.studentId == lift(studentId))
        .join(query[Owl]).on {
          case (os, o) => os.owlId == o.id
        }
        .map {
          case (os, o) => OwlPreloaded(o.displayName, o.description, o.applicableStage,
            os.owlCount, os.activeStagesLeft.isDefined)
        }
    )

  def loadForStageUpdate(studentId: Long, stage: Student.Stage): Seq[OwlStageUpdate] =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.activeStagesLeft.isDefined)
        .join(query[Owl]).on {
          case (os, o) => os.owlId == o.id && o.applicableStage.forall(_ == lift(stage))
        }
        .map {
          case (os, o) => OwlStageUpdate(o.impl, o.applicableStage, os.activeStagesLeft)
        }
    )

  def updatePostStageUpdate(studentId: Long, stage: Student.Stage): Unit =
    run(
      query[OwlsStudent].update(os =>
        os.activeStagesLeft -> os.activeStagesLeft.flatMap(s => if (s > 1) Some(s - 1) else None))
    )
}
