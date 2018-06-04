package com.github.m3lifaro.rest.route

import akka.actor.ActorRef

import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.AskSupport
import akka.util.Timeout
import com.github.m3lifaro.kafka.{EventContainer, Processed}
import com.github.m3lifaro.rest.{JsonSupport, SimpleEvent}
import com.typesafe.scalalogging.StrictLogging
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
class EventRoute(supervisor: ActorRef)(implicit executionContext: ExecutionContext) extends Directives with StrictLogging with JsonSupport with AskSupport {

  val route: Route = rawRoute ~ simpleEventRoute

  def doSomeLogic(event: String): Future[String] = {
    implicit val timeout: Timeout = Timeout(30.seconds)
    (supervisor ? EventContainer(event)) flatMap {
      case Processed ⇒ Future("Accepted")
      //todo need to process this case
      case _ ⇒ Future.failed(new Exception(s"failed process event: $event"))
    }
  }

  def rawRoute: Route = pathPrefix("api/raw") {
    entity(as[Map[String, Any]]) { rawEvent =>
      onComplete(doSomeLogic(rawEvent.toJson.compactPrint)) {
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
      onComplete(doSomeLogic(simpleEvent.toJson.compactPrint)) {
        case Success(msg) =>
          complete(StatusCodes.Accepted -> msg)
        case Failure(err) =>
          logger.error("Some error occurred", err)
          complete(StatusCodes.BadRequest -> err.getMessage)
      }
    }
  }
}
