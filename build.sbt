import sbt.Keys.version
import Dependencies._
name := "events-storing"

version := "0.1"

lazy val commonSettings = Seq(
  organization := "com.github.m3lifaro",
  scalaVersion := "2.12.4"
)

lazy val api = (project in file("api")).settings(
  commonSettings,
  name := "api",
  scalacOptions in Test ++= Seq("-Yrangepos"),
  libraryDependencies ++= Seq(akkaActor,
    akkaHttp,
    pureKafka,
    akkaHttpTestKit,
    akkaTestKit,
    kafkaTesting)
    ++ Dependencies.TestKit
).dependsOn(common)

lazy val writer = (project in file("writer")).settings(
  commonSettings,
  name := "writer",
  libraryDependencies ++= Seq(clickhouse)
    ++ Dependencies.Kafka
).dependsOn(common)

lazy val common = (project in file("common")).settings(
  commonSettings,
  name := "common",
  libraryDependencies ++= Seq(akkaHttpSpray,
    jodaTime,
    akkaStream)
    ++ Dependencies.Logging
)