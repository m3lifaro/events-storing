package com.github.m3lifaro.storage

import com.typesafe.scalalogging.StrictLogging

class SomeDBStorage() extends StrictLogging {
  def insertEvents(events: Seq[String]): Unit = logger.info(s"inserted ${events.size} events")
}
