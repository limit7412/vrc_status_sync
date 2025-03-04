package util

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.core.sync.RequestBody

object S3 {
  def putObject(
      bucket: String,
      key: String,
      body: String
  ): PutObjectResponse = {
    val s3Client = S3Client
      .builder()
      .region(software.amazon.awssdk.regions.Region.of(sys.env("AWS_REGION")))
      .build()
    val putObjectRequest = PutObjectRequest
      .builder()
      .bucket(bucket)
      .key(key)
      .build()
    val requestBody = RequestBody.fromString(body)
    s3Client.putObject(putObjectRequest, requestBody)
  }

  def getObject(bucket: String, key: String): String = {
    val s3Client = S3Client
      .builder()
      .region(software.amazon.awssdk.regions.Region.of(sys.env("AWS_REGION")))
      .build()
    val getObjectRequest =
      GetObjectRequest.builder().bucket(bucket).key(key).build()
    val response = s3Client.getObject(getObjectRequest)
    new String(response.readAllBytes(), "UTF-8")
  }
}

object VRCCookie {
  val bucket = s"vrc-status-sync-data-${sys.env("ENV")}"
  val key = "vrc_cookie.txt"

  def putObject(token: String) = {
    S3.putObject(bucket, key, token)
  }

  def getObject(): String = {
    S3.getObject(bucket, key)
  }
}
