package com.github.m3lifaro.rest

import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

trait DateTimeSupport {
  val zone: DateTimeZone = DateTimeZone.UTC

  val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val timeFormatMillis: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")
  val timeFormatMillisZ: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z")
  val timeFormatSecond: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")


  def formatTime(time: DateTime): String = timeFormatMillis.print(time)

  def formatDate(date: LocalDate): String = dateFormat.print(date)

  def formatDate(time: DateTime): String = formatDate(time.toLocalDate)

}

object DateTimeSupport extends DateTimeSupport

