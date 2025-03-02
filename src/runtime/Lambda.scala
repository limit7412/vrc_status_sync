package serverless

import sttp.client4.quick._
import upickle.default._

object Lambda {
  case class CloudWatchScheduledEventRequest(
      time: String
  ) derives ReadWriter

  case class Response(statusCode: Int = 0, body: String) derives ReadWriter
  case class ErrorResponse(statusCode: Int = 0, body: ErrorMessage)
      derives ReadWriter
  case class ErrorMessage(msg: String, error: String) derives ReadWriter

  def Handler[A: Reader](name: String, callback: A => Response): Lambda.type = {
    if (name == sys.env("_HANDLER")) {
      handler(callback)
    }

    this
  }
  private def handler[A: Reader](callback: A => Response): Lambda.type = {
    var response = quickRequest
      .get(
        uri"http://${sys.env("AWS_LAMBDA_RUNTIME_API")}/2018-06-01/runtime/invocation/next"
      )
      .send()
    val requestID = response.header("Lambda-Runtime-Aws-Request-Id")

    try {
      val decodeBody = read[A](response.body)
      val result = callback(decodeBody)

      basicRequest
        .post(
          uri"http://${sys.env("AWS_LAMBDA_RUNTIME_API")}/2018-06-01/runtime/invocation/$requestID/response"
        )
        .body(write(result))
        .send()
    } catch {
      case e: Exception => {
        e.printStackTrace()
        basicRequest
          .post(
            uri"http://${sys.env("AWS_LAMBDA_RUNTIME_API")}/2018-06-01/runtime/invocation/$requestID/error"
          )
          .body(
            write(
              ErrorResponse(
                statusCode = 500,
                body = ErrorMessage("Internal Lambda Error", e.getMessage())
              )
            )
          )
          .send()
      }
    }

    handler[A](callback)
    this
  }
}
