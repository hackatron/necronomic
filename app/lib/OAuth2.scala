package lib

import play.api.libs.ws.WS
import play.core.parsers._

case class OAuth2Settings(
  clientId: String,
  clientSecret: String,
  signInUrl: String,
  accessTokenUrl: String,
  userInfoUrl: String,
  redirectUrl: String,
  scope: String
)

abstract class OAuth2[T](settings: OAuth2Settings){
  def user(body: String): T

  import settings._
  val encodedRedirectUrl = java.net.URLEncoder.encode(redirectUrl)
  lazy val fullSignInUrl = signInUrl + "?client_id=" + clientId + "&scope=" + scope + "&redirect_uri=" + encodedRedirectUrl

  def requestAccessToken(code: String): Option[String] = {
    val resp = WS.url(accessTokenUrl +
      "?client_id=" + clientId +
      "&client_secret=" + clientSecret +
      "&code=" + code +
      "&redirect_uri=" + encodedRedirectUrl).get.value.get.body

    FormUrlEncodedParser.parse(resp).get("access_token").flatMap(_.headOption)
  }

  def authenticate(code: String) = requestAccessToken(code).map(requestUserInfo)

  def requestUserInfo(accessToken: String): T =
    user(WS.url(userInfoUrl + "?access_token=" + accessToken).get.value.get.body)
}