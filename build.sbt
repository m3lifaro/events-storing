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
  libraryDependencies ++= Seq(akkaActor,
    akkaHttp,
    akkaStream,
    akkaHttpSpray)
)