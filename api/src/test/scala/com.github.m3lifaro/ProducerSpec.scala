package com.github.m3lifaro

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import com.github.m3lifaro.kafka.{EventContainer, Processed}
import com.github.m3lifaro.rest.Recordable
import com.github.m3lifaro.rest.route.EventRoute
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.scalatest.{BeforeAndAfterAll, Failed, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Success

class ProducerSpec extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with Recordable with EmbeddedKafka {
  val probe = TestProbe()


  val producer = probe.ref
  implicit val ec = probe.system.dispatcher

  val routes = new EventRoute(producer).route

  "Producer actor" must {

    "response as Accepted" in {

      implicit val timeout: Timeout = Timeout(10.seconds)

      val future = processMsg("test")
      probe.expectMsg(10 millis, EventContainer("test"))
      probe.reply(Processed)
      assert(future.isCompleted && future.value.contains(Success(StatusCodes.Accepted → "Accepted")))
    }

    "response as Bad Request" in {

      implicit val timeout: Timeout = Timeout(10.seconds)

      implicit val ec = probe.system.dispatcher
      val future = processMsg("test error")
      probe.expectMsg(10 millis, EventContainer("test error"))
      probe.reply(Failed("error"))
      assert(future.isCompleted && future.value.contains(Success(StatusCodes.BadRequest → "error")))
    }
  }

  "runs with embedded kafka" should {

    "kafka is working" in {
      val config = EmbeddedKafkaConfig(kafkaPort = 9092)
      withRunningKafkaOnFoundPort(config) { implicit actualConfig =>
        // now a kafka broker is listening on actualConfig.kafkaPort
        publishStringMessageToKafka("testTopic", "message")
        consumeFirstStringMessageFrom("testTopic") shouldBe "message"
      }
    }
  }
}