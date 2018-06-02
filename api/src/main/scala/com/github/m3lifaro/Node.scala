package com.github.m3lifaro
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

object Node extends App {

  implicit val system: ActorSystem = ActorSystem("events-storing-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route =
    path("hello") {
      get {
        complete("Hello, world!")
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

}
