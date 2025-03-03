package vrchat

import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri
import java.util.Base64

val userAgent = "VRCStatusSync/1.0 (github.com/kairox)"

object UserRepository {
  def findByUserID(userID: String): Models.User = {
    val response =
      basicRequest
        .method(Method.GET, uri"https://api.vrchat.cloud/api/1/users/${userID}")
        .headers(
          Map(
            "Cookie" -> s"auth=${sys.env("VRC_AUTH_TOKEN")}",
            "User-Agent" -> userAgent,
            "Content-Type" -> "application/json"
          )
        )
        .send()

    val body = response.body match {
      case Right(res) => res
      case Left(e) => {
        System.err.println(s"failed get user api: ${e}")
        throw new Error(e)
      }
    }

    read[Models.User](body)
  }

  case class UpdateUserRequest(
      status: Status,
      statusDescription: String
  ) derives ReadWriter

  def updateStatus(
      userID: String,
      status: Status,
      statusDescription: String
  ): Models.User = {
    val response =
      basicRequest
        .method(Method.PUT, uri"https://api.vrchat.cloud/api/1/users/${userID}")
        .headers(
          Map(
            "Cookie" -> s"auth=${sys.env("VRC_AUTH_TOKEN")}",
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
        throw new Error(e)
      }
    }

    read[Models.User](body)
  }
}
