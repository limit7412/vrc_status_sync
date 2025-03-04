package tools

import scala.io.StdIn
import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri
import java.util.Base64
import java.net.URLEncoder

val userAgent = "test-application/1.00 qazx7412@oxymoron.link"

object LoginTool {
  // def main(args: Array[String]): Unit = {
  //   println("VRChat APIログインツール")

  //   println("ユーザー名を入力してください:")
  //   val rawUsername = StdIn.readLine().trim
  //   println("パスワードを入力してください:")
  //   val rawPassword = StdIn.readLine().trim

  //   // URLエンコード
  //   val username = URLEncoder.encode(rawUsername, "UTF-8")
  //   val password = URLEncoder.encode(rawPassword, "UTF-8")

  //   try {
  //     val authToken = Auth.login(username, password)
  //     println(s"認証成功！")
  //     println(s"認証トークン: $authToken")
  //     println("このトークンを環境変数 VRC_AUTH_TOKEN に設定してください。")
  //     println("例: export VRC_AUTH_TOKEN=\"$authToken\"")
  //   } catch {
  //     case e: Exception =>
  //       println(s"ログイン処理中にエラーが発生しました: ${e.getMessage}")
  //   }
  // }
}

object Auth {
  def login(username: String, password: String): String = {
    // Base64エンコードされた認証情報
    val authHeader = "Basic " + Base64.getEncoder.encodeToString(
      s"$username:$password".getBytes("UTF-8")
    )

    // 最初のリクエスト
    val initialResponse = basicRequest
      .method(Method.GET, uri"https://api.vrchat.cloud/api/1/auth/user")
      .header("Authorization", authHeader)
      .header("User-Agent", userAgent)
      .send()

    // 2FAが必要かどうかを確認
    val responseBody = initialResponse.body match {
      case Right(body) =>
        try {
          ujson.read(body)
        } catch {
          case e: Exception =>
            throw new Error(s"JSONのパースに失敗しました: ${e.getMessage}")
        }
      case Left(errorBody) =>
        throw new Error(s"APIリクエストに失敗しました: $errorBody")
    }

    if (responseBody.obj.contains("requiresTwoFactorAuth")) {

      // 2FAが必要な場合
      println("2段階認証が必要です。メールに送信された確認コードを入力してください:")
      val twoFactorCode = StdIn.readLine().trim

      // 2FAコードを送信
      val twoFactorResponse = basicRequest
        .method(
          Method.POST,
          uri"https://api.vrchat.cloud/api/1/auth/twofactorauth/totp/verify"
        )
        .header("Authorization", authHeader)
        .body(write(Map("code" -> twoFactorCode)))
        .contentType("application/json")
        .send()

      // 2FAレスポンスからクッキーを取得
      val cookies = twoFactorResponse.unsafeCookies
      val authCookie = cookies
        .find(_.name == "auth")
        .getOrElse(
          throw new Error("2段階認証に失敗しました。確認コードを確認してください。")
        )

      authCookie.value
    } else {
      // 2FAが不要な場合は通常のレスポンスからクッキーを取得
      val cookies = initialResponse.unsafeCookies
      val authCookie = cookies
        .find(_.name == "auth")
        .getOrElse(
          throw new Error("認証に失敗しました。ユーザー名とパスワードを確認してください。")
        )

      authCookie.value
    }
  }
}
