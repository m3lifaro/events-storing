package com.github.m3lifaro

import akka.actor.{Actor, ActorRef, Props}
import com.github.m3lifaro.config.KafkaConfig
import com.github.m3lifaro.kafka.{EventContainer, Processed, Producer}
import com.typesafe.scalalogging.StrictLogging

class SupervisorActor(config: KafkaConfig) extends Actor with StrictLogging {
  val producer: ActorRef = context.actorOf(Props(new Producer(config)))
  def receive: Receive = {
    case msg:EventContainer ⇒
      producer.forward(msg)
      sender ! Processed
    case msg ⇒
      logger.warn(s"unexpected message received: $msg")
  }
}
