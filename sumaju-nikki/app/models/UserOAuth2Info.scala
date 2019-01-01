package models

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import db.DbCtx

import scala.concurrent.{ExecutionContext, Future}

case class UserOAuth2Info(infoId: Long,
                          accessToken: String,
                          tokenType: Option[String],
                          expiresIn: Option[Int],
                          refreshToken: Option[String])

class UserOAuth2InfoDao(val db: DbCtx,
                        val loginInfoDao: UserLoginInfoDao)
                       (implicit val ec: ExecutionContext) extends DelegableAuthInfoDAO[OAuth2Info] {
  import db._

  private val schema = quote(querySchema[UserOAuth2Info]("user_oauth2_info"))

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = Future {
    run(
      schema
        .join(query[UserLoginInfo])
        .on((pi, li) => pi.infoId == li.id &&
          li.providerId == lift(loginInfo.providerID) &&
          li.providerKey == lift(loginInfo.providerKey))
        .map(_._1)
    )
      .headOption
      .map(r => OAuth2Info(r.accessToken, r.tokenType, r.expiresIn, r.refreshToken))
  }

  override def add(li: LoginInfo, oi: OAuth2Info): Future[OAuth2Info] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(schema.insert(lift(UserOAuth2Info(liId, oi.accessToken, oi.tokenType, oi.expiresIn, oi.refreshToken))))
    oi
  }

  override def update(li: LoginInfo, oi: OAuth2Info): Future[OAuth2Info] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(
      schema
        .filter(_.infoId == lift(liId))
        .update(_.accessToken -> lift(oi.accessToken),
          _.tokenType -> lift(oi.tokenType),
          _.expiresIn -> lift(oi.expiresIn),
          _.refreshToken -> lift(oi.refreshToken))
    )
    oi
  }

  override def save(li: LoginInfo, oi: OAuth2Info): Future[OAuth2Info] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(
      schema
        .insert(lift(UserOAuth2Info(liId, oi.accessToken, oi.tokenType, oi.expiresIn, oi.refreshToken)))
        .onConflictUpdate(_.infoId)(
          (r, _) => r.accessToken -> lift(oi.accessToken),
          (r, _) => r.tokenType -> lift(oi.tokenType),
          (r, _) => r.expiresIn -> lift(oi.expiresIn),
          (r, _) => r.refreshToken -> lift(oi.refreshToken))
    )
    oi
  }

  override def remove(li: LoginInfo): Future[Unit] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(schema.filter(_.infoId == lift(liId)).delete)
  }
}
