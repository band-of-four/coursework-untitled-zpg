package models

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.{ExecutionContext, Future}
import db.DbCtx

case class UserPasswordInfo(infoId: Long, hasher: String, password: String, salt: Option[String])

class UserPasswordInfoDao(val db: DbCtx,
                          val loginInfoDao: UserLoginInfoDao)
                         (implicit val ec: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {
  import db._

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    run(
      query[UserPasswordInfo]
        .join(query[UserLoginInfo])
        .on((pi, li) => pi.infoId == li.id &&
          li.providerId == lift(loginInfo.providerID) &&
          li.providerKey == lift(loginInfo.providerKey))
        .map(_._1)
    )
      .headOption
      .map(r => PasswordInfo(r.hasher, r.password, r.salt))
  }

  override def add(li: LoginInfo, pi: PasswordInfo): Future[PasswordInfo] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(query[UserPasswordInfo].insert(lift(UserPasswordInfo(liId, pi.hasher, pi.password, pi.salt))))
    pi
  }

  override def update(li: LoginInfo, pi: PasswordInfo): Future[PasswordInfo] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(
      query[UserPasswordInfo]
        .filter(_.infoId == lift(liId))
        .update(_.hasher -> lift(pi.hasher),
          _.password -> lift(pi.password),
          _.salt -> lift(pi.salt))
      )
    pi
  }

  override def save(li: LoginInfo, pi: PasswordInfo): Future[PasswordInfo] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(
      query[UserPasswordInfo]
        .insert(lift(UserPasswordInfo(liId, pi.hasher, pi.password, pi.salt)))
        .onConflictUpdate(_.infoId)(
          (r, _) => r.hasher -> lift(pi.hasher),
          (r, _) => r.password -> lift(pi.password),
          (r, _) => r.salt -> lift(pi.salt))
    )
    pi
  }

  override def remove(li: LoginInfo): Future[Unit] = Future {
    val liId = loginInfoDao.findIdByLoginInfo(li).get
    run(query[UserPasswordInfo].filter(_.infoId == lift(liId)).delete)
  }
}
