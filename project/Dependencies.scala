import sbt._

object Dependencies {
  val akkaVersion = "2.5.11"
  val akkaHttpVersion = "10.1.1"
  val logbackVersion = "1.2.3"
  val scalaLoggingVersion = "3.9.0"
  val cakeSolutionsVersion = "1.0.0"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion

  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion

  val jodaTime = "joda-time" % "joda-time" % "2.9.9"

  val kafkaClient = "net.cakesolutions" %% "scala-kafka-client" % cakeSolutionsVersion
  val akkaKafkaClient = "net.cakesolutions" %% "scala-kafka-client-akka" % cakeSolutionsVersion
  val pureKafka =  "org.apache.kafka" %% "kafka" % "1.1.0"

  val Logging = Seq(logback, scalaLogging)
  val Kafka = Seq(kafkaClient, akkaKafkaClient)
}
