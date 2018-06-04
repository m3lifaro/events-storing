package com.github.m3lifaro.rest.route

import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.github.m3lifaro.rest.{JsonSupport, SimpleEvent}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}

class EventRoute()(implicit executionContext: ExecutionContext) extends Directives with StrictLogging with JsonSupport {

  val route: Route = rawRoute ~ simpleEventRoute

  def doSomeRawLogic(event: Map[String, Any]) = Future("accepted")
  def doSomeSimpleLogic(event: SimpleEvent) = Future("accepted")

  def rawRoute: Route = pathPrefix("api/raw") {
    entity(as[Map[String, Any]]) { rawEvent =>
      onComplete(doSomeRawLogic(rawEvent)) {
        case Success(msg) =>
          complete(StatusCodes.Accepted -> msg)
        case Failure(err) =>
          logger.error("Some error occurred", err)
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }

  def simpleEventRoute: Route = pathPrefix("api/event") {
    entity(as[SimpleEvent]) { simpleEvent ⇒
      onComplete(doSomeSimpleLogic(simpleEvent)) {
        case Success(msg) =>
          complete(StatusCodes.Accepted -> msg)
        case Failure(err) =>
          logger.error("Some error occurred", err)
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }
}
