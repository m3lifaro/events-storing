import sbt._

object Dependencies {
  val akkaVersion = "2.5.11"
  val akkaHttpVersion = "10.1.1"
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaHttp = "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaHttpSpray = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion}