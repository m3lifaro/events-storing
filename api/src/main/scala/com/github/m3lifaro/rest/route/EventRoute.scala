package com.github.m3lifaro.rest.route

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.github.m3lifaro.rest.{JsonSupport, Recordable, SimpleEvent}
import com.typesafe.scalalogging.StrictLogging
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
class EventRoute(val producer: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with StrictLogging with JsonSupport with Recordable {

  val route: Route = rawRoute ~ simpleEventRoute

  def rawRoute: Route = pathPrefix("api/raw") {
    entity(as[Map[String, Any]]) { rawEvent =>
      onComplete(processMsg(rawEvent.toJson.compactPrint)) {
        case Success(resp) =>
          complete("")
        case Failure(err) =>
          logger.error("Some error occurred", err)
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }

  def simpleEventRoute: Route = pathPrefix("api/event") {
    entity(as[SimpleEvent]) { simpleEvent ⇒
      onComplete(processMsg(simpleEvent.toJson.compactPrint)) {
        case Success(resp) =>
          complete("")
        case Failure(err) =>
          logger.error("Some error occurred", err)
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }
}
