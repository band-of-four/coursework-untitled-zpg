package models

import db.{DbCtx, Pagination}
import services.SuggestionService.NoteApproved

case class Note(text: String, textGender: Student.Gender, stage: Student.Stage,
                lessonId: Option[Long] = None, clubId: Option[Long] = None, creatureId: Option[Long] = None,
                creatorId: Option[Long] = None, isApproved: Boolean = false, heartCount: Long = 0, id: Long = -1)

case class NotePreloaded(id: Long, text: String, stage: Student.Stage,
                         lesson: Option[String], club: Option[String], creature: Option[String],
                         heartCount: Long, isHearted: Boolean)

case class NoteForCreator(id: Long, text: String, gender: Student.Gender, stage: Student.Stage,
                          lesson: Option[String], club: Option[String], creature: Option[String],
                          heartCount: Long)

case class NoteForApproval(id: Long, stage: Student.Stage, gender: Student.Gender, text: String)

case class NoteForApprovalNamed(id: Long, stage: Student.Stage, gender: Student.Gender, text: String, name: String)

case class NoteHeartsUser(userId: Long, noteId: Long)

case class NoteHeartToggled(heartCount: Long, isHearted: Boolean)

class NoteDao(db: DbCtx) {
  import db._

  def initialNoteId(gender: Student.Gender): Long = gender match {
    case Student.Gender.Female => 1
    case Student.Gender.Male => 2
  }

  def toggleHeart(userId: Long, noteId: Long): Option[NoteHeartToggled] = {
    run(
      infix"""SELECT status as "_1", new_heart_count as "_2" FROM note_heart_toggle(${lift(userId)}, ${lift(noteId)})"""
        .as[Query[(String, Long)]]
    ).head match {
      case ("error", _) => None
      case ("added", heartCount) => Some(NoteHeartToggled(heartCount, isHearted = true))
      case ("removed", heartCount) => Some(NoteHeartToggled(heartCount, isHearted = false))
    }
  }

  def load(studentId: Long, noteId: Long): NotePreloaded =
    run(
      query[Note]
        .filter(_.id == lift(noteId))
        .leftJoin(query[Lesson]).on {
          case (n, l) => n.lessonId.exists(_ == l.id)
        }
        .leftJoin(query[Club]).on {
          case ((n, l), cl) => n.clubId.exists(_ == cl.id)
        }
        .leftJoin(query[Creature]).on {
          case (((n, l), cl), cr) => n.creatureId.exists(_ == cr.id)
        }
        .leftJoin(query[NoteHeartsUser]).on {
          case ((((n, l), cl), cr), heart) => heart.noteId == n.id && heart.userId == lift(studentId)
        }
        .map {
          case ((((n, l), cl), cr), heart) => NotePreloaded(
            n.id, n.text, n.stage, l.map(_.name), cl.map(_.name), cr.map(_.name), n.heartCount, heart.nonEmpty)
        }
    ).head

  def loadForCreator(creatorId: Long, pagination: Pagination): Seq[NoteForCreator] =
    run(
      query[Note]
        .filter(_.creatorId.exists(_ == lift(creatorId)))
        .sortBy(n => n.id)(Ord.desc)
        .paginate(lift(pagination))
        .nested
        .leftJoin(query[Lesson]).on {
          case (n, l) => n.lessonId.exists(_ == l.id)
        }
        .leftJoin(query[Club]).on {
          case ((n, l), cl) => n.clubId.exists(_ == cl.id)
        }
        .leftJoin(query[Creature]).on {
          case (((n, l), cl), cr) => n.creatureId.exists(_ == cr.id)
        }
        .map {
          case (((n, l), cl), cr) => NoteForCreator(
            n.id, n.text, n.textGender, n.stage, l.map(_.name), cl.map(_.name), cr.map(_.name), n.heartCount)
        }
    )

  def findIdForCurrentStage(student: StudentForUpdate): Long =
    run(
      query[Note]
        .filter(n => n.isApproved &&
          n.stage == lift(student.stage) &&
          n.textGender == lift(student.gender))
        .map(_.id)
        .takeRandom
    ).head

  def findIdForLesson(gender: Student.Gender, lessonId: Long): Long =
    run(
      query[Note]
        .filter(n => n.isApproved &&
          n.stage == lift(Student.Stage.Lesson: Student.Stage) && // hint to use the right index
          n.textGender == lift(gender) &&
          n.lessonId.exists(_ == lift(lessonId)))
        .map(_.id)
        .takeRandom
    ).head

  def findIdForClub(gender: Student.Gender, clubId: Long): Long =
    run(
      query[Note]
        .filter(n => n.isApproved &&
          n.stage == lift(Student.Stage.Club: Student.Stage) && // hint to use the right index
          n.textGender == lift(gender) &&
          n.clubId.exists(_ == lift(clubId)))
        .map(_.id)
        .takeRandom
    ).head

  def findIdForFight(student: StudentForUpdate, creatureId: Long): Long =
    run(
      query[Note]
        .filter(n => n.isApproved &&
          n.stage == lift(student.stage) &&
          n.textGender == lift(student.gender) &&
          n.creatureId.exists(_ == lift(creatureId)))
        .map(_.id)
        .takeRandom
    ).head

  def createForLesson(creatorId: Long, text: String, gender: Student.Gender, lessonId: Long): Unit =
    run(query[Note].insert(lift(
      Note(text, gender, Student.Stage.Lesson, lessonId = Some(lessonId), creatorId = Some(creatorId)))
    ).returning(_.id))

  def createForCreature(creatorId: Long, text: String, gender: Student.Gender, stage: Student.Stage, creatureId: Long): Unit =
    run(query[Note].insert(lift(
      Note(text, gender, stage, creatureId = Some(creatureId), creatorId = Some(creatorId)))
    ).returning(_.id))

  def loadFirstUnapproved(): Option[NoteForApprovalNamed] = {
    val note = run(
      query[Note]
        .filter(!_.isApproved)
        .sortBy(_.id)(Ord.asc)
        .take(1)
      ).headOption
    note match {
      case Some(n) =>
        val name = n.stage match {
          case Student.Stage.Club =>
            run(
              query[Club]
                .filter(_.id == lift(n.clubId.get))
                .map(c => c.name)
              ).head
          case Student.Stage.Lesson =>
            run(
              query[Lesson]
                .filter(_.id == lift(n.lessonId.get))
                .map(l => l.name)
              ).head
          case _ => ""
        }
        Some(NoteForApprovalNamed(n.id, n.stage, n.textGender, n.text, name))
      case None => None
    }
  }

  def applyApproved(n: NoteApproved): Unit = {
    if (!n.isApproved)
      run(query[Note].filter(_.id == lift(n.id)).delete)
    else
      run(
        query[Note]
          .filter(_.id == lift(n.id))
          .update(_.text -> lift(n.text), _.isApproved -> true)
        )
  }
}
