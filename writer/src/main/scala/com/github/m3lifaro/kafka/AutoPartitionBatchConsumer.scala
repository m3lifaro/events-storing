package com.github.m3lifaro.kafka


import akka.actor.{Actor, ActorRef, PoisonPill}
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.akka.{ConsumerRecords, Extractor, KafkaConsumerActor}
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe}
import com.github.m3lifaro.config.KafkaConsumerConfig
import com.github.m3lifaro.storage.SomeDBStorage
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.{ConsumerConfig, OffsetResetStrategy}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.duration._
import scala.util.Try

class AutoPartitionBatchConsumer(config: KafkaConsumerConfig) extends Actor with StrictLogging {

  val recordsExt: Extractor[Any, ConsumerRecords[String, String]] = ConsumerRecords.extractor[String, String]

  val consumerConf = KafkaConsumer.Conf(
    keyDeserializer = new StringDeserializer,
    valueDeserializer = new StringDeserializer,
    bootstrapServers = config.bootstrapServers,
    groupId = config.group,
    enableAutoCommit = false,
    autoCommitInterval = 1000,
    sessionTimeoutMs = 10000,
    maxPartitionFetchBytes = ConsumerConfig.DEFAULT_MAX_PARTITION_FETCH_BYTES,
    maxPollRecords = config.batchSize,
    autoOffsetReset = OffsetResetStrategy.EARLIEST
  )

  val actorConf = KafkaConsumerActor.Conf(
    scheduleInterval = 10.seconds,   // scheduling interval for Kafka polling when consumer is inactive
    unconfirmedTimeout = 3.seconds, // duration for how long to wait for a confirmation before redelivery
    maxRedeliveries = 3             // maximum number of times a unconfirmed message will be redelivered
  )

  val consumer: ActorRef = context.system.actorOf(
    KafkaConsumerActor.props(consumerConf, actorConf, self)
  )

  consumer ! Subscribe.AutoPartition(Seq(config.topic))

  val db = new SomeDBStorage()

  override def receive: Receive = {
    case recordsExt(records) =>

      logger.info(s"inserting ${records.values.length}")

      Try(db.insertEvents(records.values)).toOption match {
        case Some(_) ⇒ sender() ! Confirm(records.offsets, commit = true)
        case None ⇒ self ! PoisonPill
      }

    case _ ⇒
      logger.warn("unexpected message received")
  }


}