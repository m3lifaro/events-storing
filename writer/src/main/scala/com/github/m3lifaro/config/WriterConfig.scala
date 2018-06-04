package com.github.m3lifaro.config

import com.typesafe.config.ConfigFactory

case class KafkaConsumerConfig(bootstrapServers: String, topic: String, group: String, batchSize: Int)
object WriterConfig {
  private val config = ConfigFactory.load()
  private val bootstrapServers: String = config.getString("kafka.bootstrap")
  private val topic: String = config.getString("kafka.topic")
  private val group: String = config.getString("kafka.group")
  private val batchSize: Int = config.getInt("kafka.batch")

  val kafkaConfig = KafkaConsumerConfig(bootstrapServers, topic, group, batchSize)
}
