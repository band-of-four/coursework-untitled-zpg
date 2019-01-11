package services

import models.StudentDao
import play.api.libs.json.Json
import services.StageService.CurrentStage

import scala.concurrent.ExecutionContext

object StageService {
  case class CurrentStage(name: String)

  implicit val stageWrites = Json.writes[CurrentStage]
}

class StageService(studentDao: StudentDao)(implicit ec: ExecutionContext) {
  def getCurrentStage(userId: Long): CurrentStage = CurrentStage("h")
}
