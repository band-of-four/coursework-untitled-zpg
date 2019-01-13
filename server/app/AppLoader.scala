import java.io.Closeable

import actors.{GameProgressionActor, SocketMessengerActor}
import akka.actor.{ActorRef, Props}
import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import io.getquill._
import javax.sql.DataSource
import play.api.mvc.EssentialFilter
import utils.auth.SilhouetteLoader

class AppLoader extends ApplicationLoader {
  private var components: AppComponents = _

  override def load(ctx: Context): Application = {
    components = new AppComponents(ctx)

    if (components.configuration.get[Boolean]("game-progression.enabled"))
      components.gameProgressionActor ! GameProgressionActor.Poll

    components.application
  }
}

class AppComponents(ctx: Context) extends BuiltInComponentsFromContext(ctx)
                                  with play.filters.headers.SecurityHeadersComponents
                                  with play.api.db.DBComponents
                                  with play.api.db.evolutions.EvolutionsComponents
                                  with play.api.db.HikariCPComponents
                                  with play.api.libs.ws.ahc.AhcWSComponents
                                  with _root_.controllers.AssetsComponents {
  applicationEvolutions

  def httpFilters: Seq[EssentialFilter] = Seq(securityHeadersFilter)

  lazy val db = new _root_.db.DbCtx(CompositeNamingStrategy2(SnakeCase, PluralizedTableNames),
    dbApi.database("default").dataSource.asInstanceOf[DataSource with Closeable])

  lazy val dbExecCtx = new _root_.db.DbExecutionContext(actorSystem, "db.default.executor")

  /* DAOs */
  lazy val userDao = new _root_.models.UserDao(db)
  lazy val userLoginInfoDao = new _root_.models.UserLoginInfoDao(db, dbExecCtx)
  lazy val studentDao = new _root_.models.StudentDao(db)
  lazy val roomDao = new _root_.models.RoomDao(db)
  lazy val lessonDao = new _root_.models.LessonDao(db)
  lazy val creatureDao = new _root_.models.CreatureDao(db)
  lazy val spellDao = new _root_.models.SpellDao(db)
  lazy val studentDiaryDao = new _root_.models.StudentDiaryDao(db)
  lazy val noteDao = new _root_.models.NoteDao(db)
  /* Services */
  lazy val userService = new _root_.services.UserService(
    userDao, userLoginInfoDao, db, dbExecCtx, configuration)
  lazy val studentService = new _root_.services.StudentService(
    studentDao, spellDao, noteDao, studentDiaryDao)
  lazy val stageService = new _root_.services.StageService(
    studentDao, noteDao, studentDiaryDao)
  lazy val gameProgressionService = new _root_.services.GameProgressionService(
    stageService, roomDao, lessonDao, creatureDao, spellDao)
  /* Actors */
  lazy val socketMessengerActor: ActorRef = actorSystem.actorOf(
    Props[SocketMessengerActor], "socket-messenger-actor")
  lazy val gameProgressionActor: ActorRef = actorSystem.actorOf(
    Props(new GameProgressionActor(gameProgressionService, socketMessengerActor)), "game-progression-actor")
  /* Auth */
  lazy val silhouette = new SilhouetteLoader(configuration, userService, wsClient)
  /* Controllers */
  lazy val authController = new _root_.controllers.AuthController(
    controllerComponents, silhouette.env, silhouette.credentialsProvider, silhouette.socialProviderRegistry, userService)
  lazy val applicationController = new _root_.controllers.ApplicationController(
    controllerComponents, silhouette.env, socketMessengerActor, stageService)(materializer, executionContext, actorSystem)
  lazy val studentController = new _root_.controllers.StudentController(
    controllerComponents, silhouette.env, studentService, stageService)
  /* Routes */
  lazy val router: Router = new _root_.router.Routes(
    httpErrorHandler, applicationController, assets, authController, studentController)
}
