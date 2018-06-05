package com.github.m3lifaro

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestProbe
import akka.util.ByteString
import com.github.m3lifaro.rest.Recordable
import com.github.m3lifaro.rest.route.EventRoute
import net.manub.embeddedkafka.EmbeddedKafka
import org.scalatest._

class RouteSpec extends WordSpecLike with Matchers with BeforeAndAfterAll with Recordable with EmbeddedKafka with ScalatestRouteTest {
  val probe = TestProbe()


  val producer = probe.ref

  val routes = new EventRoute(producer).route

  "EventRoute" should {
    "Posting to /api/event should add the event" in {
      val jsonRequest = ByteString(
        s"""
           |{
           |    "data":"test",
           |    "dateTime":${DateTime.now}
           |}
        """.stripMargin)

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/api/event",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))

      postRequest ~> routes ~> check {
        status.isSuccess() shouldEqual true
      }
    }
  }
}
