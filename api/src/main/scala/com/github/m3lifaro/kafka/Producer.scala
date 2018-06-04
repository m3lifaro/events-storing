package com.github.m3lifaro.kafka

import java.util.Properties

import akka.actor.Actor
import com.github.m3lifaro.config.KafkaConfig
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

//pure kafka producer
class Producer(config: KafkaConfig) extends Actor with StrictLogging {

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  val  props = new Properties()
  props.put("bootstrap.servers", config.bootstrapServers)

  props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[Array[Byte], String](props)

  logger.info("Kafka producer started")

  override def receive: Receive = {
    case EventContainer(message) ⇒
      logger.debug(s"producing: $message")
      Future {
        producer.send(new ProducerRecord[Array[Byte], String](config.topic, message)).get()
      } onComplete {
        case Success(_) ⇒ sender() ! Processed
        case Failure(err) ⇒ sender() ! Failed(err.getMessage)
      }
    case msg ⇒
      logger.warn(s"unexpected message received: $msg")
}

  override def postStop(): Unit = {
    producer.close()
    super.postStop()
  }

}