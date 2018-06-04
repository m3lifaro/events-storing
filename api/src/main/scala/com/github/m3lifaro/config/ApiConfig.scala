package com.github.m3lifaro.config

import com.typesafe.config.{Config, ConfigFactory}

case class KafkaConfig(bootstrapServers: String, topic: String)
object ApiConfig {
  private val config: Config = ConfigFactory.load()

  private val bootstrapServers: String = config.getString("kafka.bootstrap")
  private val topic: String = config.getString("kafka.topic")

  val kafkaConfig = KafkaConfig(bootstrapServers, topic)
}
