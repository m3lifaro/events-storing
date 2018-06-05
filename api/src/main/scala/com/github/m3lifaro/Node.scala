package com.github.m3lifaro
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.m3lifaro.config.ApiConfig
import com.github.m3lifaro.kafka.Producer
import com.github.m3lifaro.rest.route.EventRoute
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContextExecutor

object Node extends App with StrictLogging {

  implicit val system: ActorSystem = ActorSystem("events-storing-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val producer = system.actorOf(Props(new Producer(ApiConfig.kafkaConfig)))

  val routes = new EventRoute(producer).route

  logger.info("Application started")

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 9000)

}
