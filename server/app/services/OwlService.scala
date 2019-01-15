package services

import play.api.libs.json.JsValue
import models.{Owl => _, _}
import owls._

import scala.concurrent.{ExecutionContext, Future}

object OwlService {
  case class OwlApplicationResult()
}

class OwlService(owlDao: OwlDao,
                 studentDao: StudentDao,
                 relationshipDao: StudentRelationshipDao)
                (implicit ec: ExecutionContext) {
  val owlImplMap: Map[String, Owl] = Map(
    "StageSkip" -> new StageSkipOwl(),
    "StudentLetter" -> new StudentLetterOwl(relationshipDao)
  )

  def getSorted(userId: Long): Future[Seq[OwlPreloaded]] = Future {
    owlDao.load(userId).sortBy(o => (o.isActive, o.name))
  }

  def apply(userId: Long, owlId: Long, payload: JsValue): Future[Boolean] = Future {
    if (owlDao.apply(userId, owlId)) {
      val owl = owlDao.findById(owlId)
      owl.applicableStage match {
        case None =>
          owlImplMap(owl.impl).apply(userId, payload)
        case Some(stage) if studentDao.findStageForUser(userId) == stage =>
          owlImplMap(owl.impl).apply(userId, payload)
        case _ =>
      }
      true
    }
    else false
  }

  def useActiveOwlsForUpdate[T](student: StudentForUpdate)(f: Seq[OwlStageUpdate] => T): T = {
    val updateResult = f(owlDao.loadForStageUpdate(student.id, student.stage))
    owlDao.updatePostStageUpdate(student.id, student.stage)
    updateResult
  }
}
