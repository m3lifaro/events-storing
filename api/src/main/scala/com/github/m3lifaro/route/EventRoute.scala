package com.github.m3lifaro.route

import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import scala.concurrent.{ExecutionContext, Future}

class EventRoute()(implicit executionContext: ExecutionContext) extends Directives {

  val route: Route = eventRoute

  def doSomeLogic(event: String) = Future("accepted")

  def eventRoute: Route = pathPrefix("api/events") {
    entity(as[String]) { event =>
      onComplete(doSomeLogic(event)) {
        case Success(msg) =>
          complete(StatusCodes.Accepted -> msg)
        case Failure(err) =>
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }
}
