package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import lib._

object Auth extends Controller {
  val FACEBOOK = new OAuth2[FacebookUser](OAuth2Settings(
    Play.current.configuration.getString("facebook.appId").get,
    Play.current.configuration.getString("facebook.appSecret").get,
    "https://www.facebook.com/dialog/oauth",
    "https://graph.facebook.com/oauth/access_token",
    "https://graph.facebook.com/me",
    Play.current.configuration.getString("application.host").get + routes.Auth.callback,
    "email"
  )) {
    def user(body: String) = Json.fromJson(Json.parse(body))
  }

  case class FacebookUser(
    name: String,
    email: String
  )

  implicit def FacebookUserReads: Reads[FacebookUser] = new Reads[FacebookUser] {
    def reads(json: JsValue) =
      FacebookUser(
        (json \ "name").as[String],
        (json \ "email").as[String]
      )
  }

  def signin() = Action { Redirect(FACEBOOK.fullSignInUrl) }

  def signout() = Action { Redirect(routes.Application.index).withSession() }

  def callback() = Action { implicit request =>
    params("code").flatMap { code =>
      FACEBOOK.authenticate(code) map { user =>
        Redirect(routes.Application.index()).withSession("email" -> user.email)
      }
    } getOrElse Redirect(routes.Application.index())
  }

  protected def params[T](key: String)(implicit request: Request[T]) = request.queryString.get(key).flatMap(_.headOption)
}
