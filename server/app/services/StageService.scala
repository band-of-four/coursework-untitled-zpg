package services

import models.StudentDao
import play.api.libs.json.Json
import services.StageService.CurrentStage
import java.time.ZoneId

import scala.concurrent.ExecutionContext

object StageService {
<<<<<<< HEAD
  case class CurrentStage(name: String)

  implicit val stageWrites = Json.writes[CurrentStage]
}

class StageService(studentDao: StudentDao)(implicit ec: ExecutionContext) {
  def getCurrentStage(userId: Long): CurrentStage = CurrentStage("h")
=======
  case class CurrentStage(name: String, startTime: Long, endTime: Long, currTime:Long)
}

class StageService(studentDao: StudentDao)(implicit ec: ExecutionContext) {
  def getCurrentStage(userId: Long): CurrentStage = {
    val student = studentDao.findForUser(userId).get
    CurrentStage(student.stage,
      student.stageStartTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), 
      student.nextStageTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
      System.currentTimeMillis())
  }
>>>>>>> stage service
}
