import play.api._
import play.api.ApplicationLoader.Context
import play.api.routing.Router
import io.getquill._

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
                                  with _root_.controllers.AssetsComponents {
  applicationEvolutions

  lazy val db = new _root_.db.DbCtx(SnakeCase, "default")

  lazy val users = new _root_.models.Users(db)

  lazy val homeController = new _root_.controllers.HomeController(controllerComponents)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, homeController, assets)
}
