package com.github.m3lifaro

import akka.actor.{ActorSystem, Props}
import com.github.m3lifaro.config.WriterConfig
import com.github.m3lifaro.kafka.AutoPartitionBatchConsumer
import com.typesafe.scalalogging.StrictLogging

object Node extends App with StrictLogging {

  logger.info("Application started")

  implicit val system: ActorSystem = ActorSystem("writer-system")
  val consumer = system.actorOf(Props(new AutoPartitionBatchConsumer(WriterConfig.kafkaConfig)))

}
