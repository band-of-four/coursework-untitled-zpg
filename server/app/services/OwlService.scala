package services

import play.api.libs.json.JsValue
import models.{Owl => _, _}
import owls._
import services.OwlService._

import scala.concurrent.{ExecutionContext, Future}

object OwlService {
  sealed trait OwlApplicationStatus
  case class ImmediateApplied(message: String) extends OwlApplicationStatus
  case class ImmediateFailed(message: String) extends OwlApplicationStatus
  case object NonImmediateApplied extends OwlApplicationStatus
  case object NotApplicable extends OwlApplicationStatus
}

class OwlService(owlDao: OwlDao,
                 studentDao: StudentDao,
                 relationshipDao: StudentRelationshipDao)
                (implicit ec: ExecutionContext) {
  val owlImplMap: Map[String, Owl] = Map(
    "StageSkip" -> new StageSkipOwl(),
    "StudentLetter" -> new StudentLetterOwl(studentDao, relationshipDao),
    "HealingOwl" -> new HealingOwl(studentDao)
  )

  def getSorted(userId: Long): Future[Seq[OwlPreloaded]] = Future {
    owlDao.load(userId).sortBy(o => (o.isActive, o.name))
  }

  def apply(userId: Long, owlImpl: String, payload: JsValue): Future[OwlApplicationStatus] = Future {
    owlDao.findAvailable(userId, owlImpl) match {
      case None =>
        NotApplicable
      case Some(owl) if !owl.isImmediate =>
        owlDao.applyNonImmediate(userId, owl)
        NonImmediateApplied
      case Some(owl) if owl.applicableStages.forall(_.contains(studentDao.findStageForUser(userId))) =>
        owlDao.applyImmediate(userId, owlImpl) {
          owlImplMap(owlImpl).apply(userId, payload)
        } match {
          case Right(successMessage) => ImmediateApplied(successMessage)
          case Left(errorMessage) => ImmediateFailed(errorMessage)
        }
      case _ =>
        ImmediateFailed("Сова не сможет достичь своей цели, дождись походящего момента.")
    }
  }

  def useActiveOwlsForUpdate[T](student: StudentForUpdate)(f: Seq[String] => T): T = {
    val owlImpls = owlDao.loadForStageUpdate(student.id, student.stage)
    val updateResult = f(owlImpls)
    owlDao.updatePostStageUpdate(student.id, owlImpls)
    updateResult
  }
}
