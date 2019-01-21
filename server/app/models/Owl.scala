package models

import db.DbCtx

case class Owl(impl: String, displayName: String, description: String,
               applicableStages: Option[Seq[Student.Stage]], stagesActive: Int, isImmediate: Boolean)

case class OwlsStudent(studentId: Long, owlImpl: String, owlCount: Int, activeStagesLeft: Option[Int])

case class OwlPreloaded(impl: String, name: String, description: String, applicableStages: Option[Seq[Student.Stage]],
                        owlCount: Int, isActive: Boolean)

class OwlDao(db: DbCtx) {
  import db._

  def addRandomToStudent(studentId: Long): Unit =
    run(infix"""SELECT owls_add_random_to_student(${lift(studentId)})""".as[Query[String]])

  def load(studentId: Long): Seq[OwlPreloaded] =
    run(
      query[OwlsStudent]
        .filter(_.studentId == lift(studentId))
        .join(query[Owl]).on {
          case (os, o) => os.owlImpl == o.impl
        }
        .map {
          case (os, o) => OwlPreloaded(o.impl, o.displayName, o.description, o.applicableStages,
            os.owlCount, os.activeStagesLeft.isDefined)
        }
    )

  def findAvailable(studentId: Long, owlImpl: String): Option[Owl] =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.owlImpl == lift(owlImpl) && os.owlCount > 0 && os.activeStagesLeft.isEmpty)
        .join(query[Owl]).on {
          case (os, o) => os.owlImpl == o.impl
        }
        .map(_._2)
    ).headOption

  def applyNonImmediate(studentId: Long, owl: Owl): Unit =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.owlImpl == lift(owl.impl))
        .update(os => os.owlCount -> (os.owlCount - 1),
          _.activeStagesLeft -> lift(Some(owl.stagesActive): Option[Int]))
    )

  def applyImmediate[L, R](studentId: Long, owlImpl: String)(apply: => Either[L, R]): Either[L, R] =
    transaction {
      apply match {
        case ok @ Right(_) =>
          run(
            query[OwlsStudent]
              .filter(os => os.studentId == lift(studentId) && os.owlImpl == lift(owlImpl))
              .update(os => os.owlCount -> (os.owlCount - 1))
          )
          ok
        case error =>
          error
      }
    }

  def loadForStageUpdate(studentId: Long, stage: Student.Stage): Seq[String] =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && os.activeStagesLeft.isDefined)
        .join(query[Owl]).on {
          case (os, o) => os.owlImpl == o.impl && o.applicableStages.exists(_.contains(lift(stage)))
        }
        .map(_._2.impl)
    )

  def updatePostStageUpdate(studentId: Long, owlImpls: Seq[String]): Unit =
    run(
      query[OwlsStudent]
        .filter(os => os.studentId == lift(studentId) && lift(owlImpls).contains(os.owlImpl))
        .update(os => os.activeStagesLeft -> os.activeStagesLeft.flatMap(s => if (s > 1) Some(s - 1) else None))
    )

  def giveLevelUp(studentId: Long): Unit =
    run(
      query[OwlsStudent]
        .insert(_.studentId -> lift(studentId), _.owlImpl -> "LevelUpOwl", _.owlCount -> 1)
      )
}
