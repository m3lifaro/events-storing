import sbt._

object Dependencies {
  val akkaVersion = "2.5.11"
  val akkaHttpVersion = "10.1.1"
  val logbackVersion = "1.2.3"
  val scalaLoggingVersion = "3.9.0"
  val cakeSolutionsVersion = "1.0.0"
  val clickhouseVerion = "0.1.39"
  val kafkaVersion = "1.1.0"
  val jodaVerion = "2.9.9"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test

  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion

  val jodaTime = "joda-time" % "joda-time" % jodaVerion

  val kafkaClient = "net.cakesolutions" %% "scala-kafka-client" % cakeSolutionsVersion
  val akkaKafkaClient = "net.cakesolutions" %% "scala-kafka-client-akka" % cakeSolutionsVersion
  val pureKafka =  "org.apache.kafka" %% "kafka" % kafkaVersion
  val kafkaTesting = "net.manub" %% "scalatest-embedded-kafka" % "1.1.0-kafka1.1-nosr" % "test"

  val clickhouse = "ru.yandex.clickhouse" % "clickhouse-jdbc" % clickhouseVerion

  val scalaTest = "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
  val specs2 = "org.specs2" %% "specs2-core" % "4.2.0" % "test"

  val TestKit = Seq(scalaTest, specs2)
  val Logging = Seq(logback, scalaLogging)
  val Kafka = Seq(kafkaClient, akkaKafkaClient)
}
