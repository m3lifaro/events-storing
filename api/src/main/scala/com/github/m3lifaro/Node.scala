package com.github.m3lifaro
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.github.m3lifaro.route.EventRoute

import scala.concurrent.ExecutionContextExecutor

object Node extends App {

  implicit val system: ActorSystem = ActorSystem("events-storing-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val routes = new EventRoute().route

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 9000)

}
