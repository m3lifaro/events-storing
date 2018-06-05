package com.github.m3lifaro.config

import com.typesafe.config.ConfigFactory

case class KafkaConsumerConfig(bootstrapServers: String, topic: String, group: String, batchSize: Int)
case class ClickhouseConfig(user: String, password: String, database: String, uri: String)

object WriterConfig {
  private val config = ConfigFactory.load()

  private val bootstrapServers: String = config.getString("kafka.bootstrap")
  private val topic: String = config.getString("kafka.topic")
  private val group: String = config.getString("kafka.group")
  private val batchSize: Int = config.getInt("kafka.batch")

  val kafkaConfig = KafkaConsumerConfig(bootstrapServers, topic, group, batchSize)

  private val user = config.getString("kafka.bootstrap")
  private val password: String = config.getString("kafka.bootstrap")
  private val database: String = config.getString("kafka.topic")
  private val uri: String = config.getString("kafka.group")

  val chConfig = ClickhouseConfig(user, password, database, uri)
}
