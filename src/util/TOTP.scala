package util

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base32

class TOTP(secret: String) {
  private val base32 = new Base32()
  private val key = base32.decode(secret.toUpperCase())

  def now(): String = {
    val time = System.currentTimeMillis() / 1000L / 30L
    generateTOTP(time)
  }

  private def generateTOTP(time: Long): String = {
    val data = java.nio.ByteBuffer.allocate(8).putLong(time).array()
    val hash = hmacSHA1(key, data)
    val offset = hash(hash.length - 1) & 0xf
    val binary = ((hash(offset) & 0x7f) << 24) |
      ((hash(offset + 1) & 0xff) << 16) |
      ((hash(offset + 2) & 0xff) << 8) |
      (hash(offset + 3) & 0xff)
    val otp = binary % 1000000
    String.format("%06d", otp)
  }

  private def hmacSHA1(key: Array[Byte], data: Array[Byte]): Array[Byte] = {
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(new SecretKeySpec(key, "HmacSHA1"))
    mac.doFinal(data)
  }
}
