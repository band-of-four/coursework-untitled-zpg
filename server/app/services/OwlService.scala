package services

import play.api.libs.json.JsValue
import models.{Owl => _, _}
import owls._

import scala.concurrent.{ExecutionContext, Future}

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

  def useActiveOwlsForUpdate[T](student: StudentForUpdate)(f: Seq[String] => T): T = {
    val (owlIds, owlImpls) = owlDao.loadForStageUpdate(student.id, student.stage).unzip
    val updateResult = f(owlImpls)
    owlDao.updatePostStageUpdate(student.id, owlIds)
    updateResult
  }
}
