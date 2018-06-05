package com.github.m3lifaro.rest

import akka.actor.ActorRef
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.pattern.AskSupport
import akka.util.Timeout
import com.github.m3lifaro.kafka.{EventContainer, Failed, Processed}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait Recordable extends AskSupport with StrictLogging {
  val producer: ActorRef
  def processMsg(event: String)(implicit ec: ExecutionContext): Future[(StatusCode, String)] = {
    implicit val timeout: Timeout = Timeout(30.seconds)
    (producer ? EventContainer(event)) flatMap {
      case Processed ⇒ Future(StatusCodes.Accepted → "Accepted")
      //todo need to process this case
      case Failed(msg) ⇒ Future(StatusCodes.BadRequest → msg)
      case _ ⇒ Future.failed(new Exception(s"failed process event: $event"))
    }
  }
}
