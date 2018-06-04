package com.github.m3lifaro.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.{DateTime, LocalDate}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsArray, JsBoolean, JsFalse, JsNull, JsNumber, JsObject, JsString, JsTrue, JsValue, JsonFormat, RootJsonFormat, deserializationError, serializationError}

import scala.util.Try

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol with StrictLogging {

  implicit val simpleEventFormat: RootJsonFormat[SimpleEvent] = jsonFormat2(SimpleEvent)

  implicit object JDateFormat extends RootJsonFormat[LocalDate] {
    override def write(date: LocalDate): JsValue = JsString(DateTimeSupport.dateFormat.print(date))

    override def read(json: JsValue): LocalDate = json match {
      case JsString(str) => DateTimeSupport.dateFormat.parseLocalDate(str)
      case other => throw DeserializationException(s"Date string expected: $other")
    }
  }

  implicit object JDateTimeFormat extends RootJsonFormat[DateTime] {
    override def write(date: DateTime): JsValue = JsString(DateTimeSupport.timeFormatMillis.print(date))

    override def read(json: JsValue): DateTime = json match {
      case JsString(str) => Try(DateTimeSupport.timeFormatMillis.parseDateTime(str))
        .getOrElse(Try(DateTimeSupport.timeFormatSecond.parseDateTime(str))
          .getOrElse(Try(DateTimeSupport.timeFormatMillisZ.parseDateTime(str))
            .getOrElse(DateTimeSupport.dateFormat.parseDateTime(str))))
      case other => throw DeserializationException(s"Date string expected: $other")
    }
  }

  implicit object AnyJsonFormat extends JsonFormat[Any] {
    def write(x: Any): JsValue = x match {
      case n: Int ⇒ JsNumber(n)
      case l: Long ⇒ JsNumber(l)
      case d: Double ⇒ JsNumber(d)
      case f: Float ⇒ JsNumber(f)
      case s: String ⇒ JsString(s)
      case x: Seq[_] ⇒ seqFormat[Any].write(x)
      case m: Map[_, _] ⇒
        mapFormat[String, Any].write(m.map{ pair ⇒ pair._1.toString → pair._2 })
      case b: Boolean ⇒ if (b) JsTrue else JsFalse
      case None ⇒ JsNull
      case null ⇒ JsNull
      case jDate: org.joda.time.DateTime ⇒ JsString(DateTimeSupport.timeFormatMillis.print(jDate))
      case lDate: org.joda.time.LocalDate ⇒ JsString(DateTimeSupport.dateFormat.print(lDate))
      case _ ⇒
        serializationError(s"Failed to serialize object of type ${x.getClass.getName}")
    }

    def read(value: JsValue): Any = value match {
      case JsNumber(n) ⇒
        n.toDouble
      case JsString(s) ⇒ s
      case a: JsArray ⇒ listFormat[Any].read(value)
      case o: JsObject ⇒ mapFormat[String, Any].read(value)
      case JsTrue ⇒ 1
      case JsFalse ⇒ 0
      case b: JsBoolean ⇒ if (b.value) 1 else 0
      case JsNull ⇒ None
      case x ⇒ deserializationError(s"Failed to deserialize $x")
    }
  }
}
