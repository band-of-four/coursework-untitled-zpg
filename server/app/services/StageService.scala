package services

import models.StudentDao
import services.StageService.CurrentStage

import scala.concurrent.ExecutionContext

object StageService {
  case class CurrentStage(name: String)
}

class StageService(studentDao: StudentDao)(implicit ec: ExecutionContext) {
  def getCurrentStageJson(userId: Long): CurrentStage = ???
}
