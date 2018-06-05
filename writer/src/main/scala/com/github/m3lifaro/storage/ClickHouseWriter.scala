package com.github.m3lifaro.storage

import com.github.m3lifaro.config.ClickhouseConfig
import java.sql.Connection
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, LocalDate}
import ru.yandex.clickhouse.ClickHouseDataSource
import ru.yandex.clickhouse.settings.ClickHouseProperties
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

class ClickHouseWriter(config: ClickhouseConfig) extends StrictLogging {
  private val properties = new ClickHouseProperties()
  properties.setConnectionTimeout(5000)
  properties.setUser(config.user)
  properties.setPassword(config.password)
  properties.setDatabase(config.database)
  private val dataSource: ClickHouseDataSource = new ClickHouseDataSource(config.uri, properties)
  private val connection: Connection = dataSource.getConnection()

  val timeFormatMillis: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

  private def execute(query: String): Boolean = {
    Try {
      val statement = connection.createStatement()
      val isCompleted = statement.execute(query)
      statement.closeOnCompletion()
      isCompleted
    } match {
      case Success(status) ⇒ status
      case Failure(e) ⇒ logger.error(s"cant execute: $query", e); throw e
    }
  }

  private def getValueFromMap(event: Map[String, Any], key: String): String = {
    event.get(key) match {
      case Some(value: String) ⇒ value
      case Some(value: DateTime) ⇒ value.toString("yyyy-MM-dd HH:mm:ss")
      case Some(value: LocalDate) ⇒ value.toString("yyyy-MM-dd")
      case Some(value: Boolean) ⇒ if (value) "1" else "0"
      case Some(value) ⇒ value.toString
      case None ⇒ s""
    }
  }

  def eventToValues(event: Map[String, Any]): String = {
    val eventDate = quoteAnyClickhouse(getValueFromMap(event, "event_date"))
    val event_time = quoteAnyClickhouse(timeFormatMillis.parseDateTime(getValueFromMap(event, "event_time")))

    val fields = event - "event_date" - "event_time"

    val nameBuffer : ListBuffer[String] = ListBuffer.empty
    val strBuffer : ListBuffer[String] = ListBuffer.empty
    val dblBuffer : ListBuffer[String] = ListBuffer.empty
    fields.foreach{ case (k,v) ⇒
      val doubleValue = v match {
        case d: Double ⇒ d
        case i: Int ⇒ i.toDouble
        case b: Boolean ⇒ if (b) 1.0 else 0.0
        case _ ⇒ 0.0
      }
      nameBuffer += quoteAnyClickhouse(k)
      val strValue = v match {
        case Seq(h, tail@_*) ⇒
          (h +: tail).map(v ⇒ doubleQuote(stringifyClickhouse(v))).mkString("[", ",", "]")

        case msg => msg.toString
      }
      strBuffer += quoteAnyClickhouse(strValue)
      dblBuffer += quoteAnyClickhouse(doubleValue)
    }
    s"($eventDate, $event_time, ${nameBuffer.mkString("[",",","]")}, ${strBuffer.mkString("[",",","]")}, ${dblBuffer.mkString("[",",","]")})"
  }
  def insertEvents(events: Seq[Map[String, Any]]): Boolean = {
    val eventsVALUES = events.map(eventToValues).mkString(",")
    val query = s"INSERT INTO ${config.database}.events (event_date, event_time, params.name, params.string_value, params.double_value) VALUES $eventsVALUES"
    execute(query)
  }

  def quote(value: String): String = s"'${value.replace("'", "")}'"
  def doubleQuote(value: String): String = s""""${value.replace("'", "").replace(""""""","")}""""

  def stringifyClickhouse(value: Any): String = {
    value match {
      case s: String ⇒ s.replace('\t', ' ').replace("\\", "\\ ")
      case t: DateTime ⇒ formatTimeClickhouse(t)
      case other ⇒ other.toString
    }
  }
  val timeFormatSecond: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

  def formatTimeClickhouse(time: DateTime): String = timeFormatSecond.print(time)
  def quoteAnyClickhouse(value: Any): String = value match {
    case null ⇒ "null"
    case Seq(h, tail@_*) ⇒ (h +: tail).map(v ⇒ quote(stringifyClickhouse(v))).mkString("[", ",", "]")
    case Nil ⇒ "[]"
    case s: String ⇒ quote(stringifyClickhouse(value))
    case t: DateTime ⇒ quote(stringifyClickhouse(value))
    case i: Int ⇒ stringifyClickhouse(value)
    case b: Boolean ⇒ stringifyClickhouse(if(b) 1 else 0)
    case d: Double ⇒ stringifyClickhouse(value)
    case fl: Float ⇒ stringifyClickhouse(value)
    case _ ⇒ quote(stringifyClickhouse(value))
  }
}
