package discord

import sttp.client4.quick._
import sttp.client4.Response
import sttp.model.StatusCode
import upickle.default._

object Repository {

  /**
   * Sets the user's "Playing" status on Discord.
   *
   * NOTE: Based on the official Discord API documentation (OpenAPI spec),
   * there is no REST API endpoint to change a user's presence. Presence updates
   * must be sent over a persistent WebSocket (Gateway) connection.
   * This is not feasible in a serverless environment.
   *
   * This function is a placeholder and will not work as intended.
   * It requires a valid Discord User Token with the 'rpc.activities.write' scope.
   *
   * @param token The user's OAuth2 token.
   * @param text The text to display (e.g., "Play Now").
   */
  def setPlayingStatus(token: String, text: String): Unit = {
    // This is a hypothetical endpoint, as the real one does not exist in the REST API.
    val endpoint = uri"https://discord.com/api/v10/users/@me/presence"

    // This is a hypothetical payload structure.
    val payload = ujson.Obj(
      "activities" -> ujson.Arr(
        ujson.Obj(
          "name" -> text,
          "type" -> 0 // 0 corresponds to "Playing"
        )
      ),
      "status" -> "online",
      "afk" -> false,
      "since" -> ujson.Null
    )

    val request = quickRequest
      .put(endpoint)
      .auth.bearer(token)
      .header("Content-Type", "application/json")
      .body(write(payload))

    // In a real implementation, we would send the request and handle the response.
    // However, since the endpoint is not real, we will just log the intent.
    println(s"Intending to set Discord status: PUT ${endpoint} with body ${write(payload)}")
    // val response = request.send()
    // println(s"Discord API response: ${response.code} ${response.body}")
  }
}
