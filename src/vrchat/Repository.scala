package vrchat

import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri
import java.util.Base64
import java.net.URLEncoder

val userAgent = "VRCStatusSync/1.0 (github.com/kairox)"

object AuthRepository {
  // tokenを返却
  def login(): String = {
    val username = URLEncoder.encode(sys.env("VRC_USERNAME"), "UTF-8")
    val password = URLEncoder.encode(sys.env("VRC_PASSWORD"), "UTF-8")

    // Base64エンコードされた認証情報
    val authHeader = "Basic " + Base64.getEncoder.encodeToString(
      s"$username:$password".getBytes("UTF-8")
    )

    // 最初のリクエスト
    val initialResponse = basicRequest
      .method(Method.GET, uri"https://api.vrchat.cloud/api/1/auth/user")
      .header("Authorization", authHeader)
      .header("User-Agent", userAgent)
      .header("Content-Type", "application/json")
      .send()

    // 2FAが必要かどうかを確認
    val responseBody = initialResponse.body match {
      case Right(body) =>
        try {
          ujson.read(body)
        } catch {
          case e: Exception =>
            throw new Exception(s"JSONのパースに失敗しました: ${e.getMessage}")
        }
      case Left(errorBody) =>
        throw new Exception(s"APIリクエストに失敗しました: $errorBody")
    }

    val totp = new util.TOTP(sys.env("VRC_TOTP_SECRET"))
    val twoFactorCode = totp.now()

    val twoFactorResponse = basicRequest
      .method(
        Method.POST,
        uri"https://api.vrchat.cloud/api/1/auth/twofactorauth/totp/verify"
      )
      .header("Authorization", authHeader)
      .header("Content-Type", "application/json")
      .body(write(Map("code" -> twoFactorCode)))
      .send()

    val body = twoFactorResponse.body match {
      case Right(res) => {
        System.err.println(s"2FA response: ${res}")
        res
      }
      case Left(e) => {
        System.err.println(s"failed two factor auth api: ${e}")
        throw new Exception(e)
      }
    }

    // 2FA認証成功時はSet-Cookieヘッダーから認証トークンを直接抽出
    val responseJson = ujson.read(body)
    System.err.println(s"Response JSON: ${responseJson}")
    System.err.println(s"Response headers: ${twoFactorResponse.headers}")

    val token = twoFactorResponse.headers
      .filter(_.name.toLowerCase == "set-cookie")
      .map(_.value)
      .find(_.startsWith("auth="))
      .map(_.substring(5).takeWhile(_ != ';'))
      .getOrElse(
        throw new Exception("auth cookie not found in Set-Cookie header")
      )

    System.err.println(s"Extracted auth token: ${token}")
    token
  }
}

object UserRepository {
  def findByUserID(token: String, userID: String): Models.User = {
    val response =
      basicRequest
        .method(Method.GET, uri"https://api.vrchat.cloud/api/1/users/${userID}")
        .headers(
          Map(
            "Cookie" -> s"auth=${token}",
            "User-Agent" -> userAgent,
            "Content-Type" -> "application/json"
          )
        )
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"failed get user api: ${e}")
        throw new Exception(e)
      }
    }

    read[Models.User](body)
  }

  case class UpdateUserRequest(
      status: Status,
      statusDescription: String
  ) derives ReadWriter

  def updateStatus(
      token: String,
      userID: String,
      status: Status,
      statusDescription: String
  ): Models.User = {
    val response =
      basicRequest
        .method(Method.PUT, uri"https://api.vrchat.cloud/api/1/users/${userID}")
        .headers(
          Map(
            "Cookie" -> s"auth=${token}",
            "User-Agent" -> userAgent,
            "Content-Type" -> "application/json"
          )
        )
        .body(write(UpdateUserRequest(status, statusDescription)))
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"failed put user api: ${e}")
        throw new Exception(e)
      }
    }

    read[Models.User](body)
  }
}
