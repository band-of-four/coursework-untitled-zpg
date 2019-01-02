import java.io.Closeable

import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import io.getquill._
import javax.sql.DataSource
import utils.auth.SilhouetteLoader

class AppLoader extends ApplicationLoader {
  private var components: AppComponents = _

  override def load(ctx: Context): Application = {
    components = new AppComponents(ctx)
    components.application
  }
}

class AppComponents(ctx: Context) extends BuiltInComponentsFromContext(ctx)
                                  with play.api.db.DBComponents
                                  with play.api.db.evolutions.EvolutionsComponents
                                  with play.api.db.HikariCPComponents
                                  with play.filters.HttpFiltersComponents
                                  with play.api.libs.ws.ahc.AhcWSComponents
                                  with _root_.controllers.AssetsComponents {
  applicationEvolutions

  lazy val db = new _root_.db.DbCtx(SnakeCase,
    dbApi.database("default").dataSource.asInstanceOf[DataSource with Closeable])

  lazy val dbExecCtx = new _root_.db.DbExecutionContext(actorSystem, "db.default.executor")

  lazy val userDao = new _root_.models.UserDao(db)
  lazy val userLoginInfoDao = new _root_.models.UserLoginInfoDao(db, dbExecCtx)

  lazy val userService = new _root_.services.UserService(
    userDao, userLoginInfoDao, db, dbExecCtx, configuration)

  lazy val silhouette = new SilhouetteLoader(configuration, userService, wsClient)

  lazy val homeController = new _root_.controllers.HomeController(controllerComponents)
  lazy val authController = new _root_.controllers.AuthController(
    controllerComponents, silhouette.env, silhouette.credentialsProvider, silhouette.socialProviderRegistry, userService)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, homeController, assets, authController)
}
