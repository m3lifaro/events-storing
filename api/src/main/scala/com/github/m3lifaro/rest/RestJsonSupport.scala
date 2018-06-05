package com.github.m3lifaro.rest

import com.github.m3lifaro.common.KafkaJsonSupport
import spray.json.RootJsonFormat

trait RestJsonSupport extends KafkaJsonSupport {

  implicit val simpleEventFormat: RootJsonFormat[SimpleEvent] = jsonFormat2(SimpleEvent)

}
