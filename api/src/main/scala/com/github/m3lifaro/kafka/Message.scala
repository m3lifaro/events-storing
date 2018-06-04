package com.github.m3lifaro.kafka

case class EventContainer(msg: String)

case object Processed

case class Failed(msg: String)
