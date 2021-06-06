name := "LearningAkkaThroughExamples"
version := "1.0"

scalaVersion := "2.13.6"

scalacOptions := Seq("-unchecked", "-deprecation")

val AkkaVersion = "2.6.14"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test

// Stream
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-stream" % AkkaVersion,
	"com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
	"com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
)

// Actor System
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
	"com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
	"ch.qos.logback" % "logback-classic" % "1.2.3",
)
