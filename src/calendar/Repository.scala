package calendar

import sttp.client4.quick._
import upickle.default._
import sttp.model.Method
import sttp.model.Uri
import java.io.FileInputStream
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.google.auth.oauth2.GoogleCredentials
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.http.HttpRequestInitializer
import com.google.auth.http.HttpCredentialsAdapter
import scala.jdk.CollectionConverters._
import com.google.api.services.calendar.CalendarScopes
import java.util.Collections
import java.util.Arrays

object repositoryInitializer {
  val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
  val jsonFactory = GsonFactory.getDefaultInstance()
  val credential = GoogleCredentials
    .fromStream(new FileInputStream("google_credential.json"))
    .createScoped(
      Arrays.asList(
        CalendarScopes.CALENDAR_READONLY,
        CalendarScopes.CALENDAR_EVENTS_READONLY
      )
    )
  val requestInitializer = new HttpCredentialsAdapter(credential)
}

object EventRepository {
  def findByDay(calendarID: String, day: LocalDate) = {
    // JSTタイムゾーンを設定
    val jstZone = ZoneId.of("Asia/Tokyo")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    // 日付の開始と終了をJSTで設定し、ISO形式に変換
    val timeMin = day.atStartOfDay(jstZone).format(formatter) + "+09:00"
    val timeMax =
      day.plusDays(1).atStartOfDay(jstZone).format(formatter) + "+09:00"

    val calendar =
      new Calendar.Builder(
        repositoryInitializer.httpTransport,
        repositoryInitializer.jsonFactory,
        repositoryInitializer.requestInitializer
      )
        .setApplicationName("vrc_status_sync")
        .build()

    val events = calendar
      .events()
      .list(calendarID)
      .setTimeMin(new com.google.api.client.util.DateTime(timeMin))
      .setTimeMax(new com.google.api.client.util.DateTime(timeMax))
      .setSingleEvents(true)
      .setTimeZone("Asia/Tokyo")
      .execute()

    events.getItems.asScala.toList
  }
}
