name := "LearningAkkaThroughExamples"
version := "1.0"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "3.0.8" % "test"
)
scalacOptions := Seq("-unchecked", "-deprecation")

val AkkaVersion = "2.6.14"
libraryDependencies ++= Seq(
	"com.typesafe" % "config" % "1.3.1",
	"com.typesafe.akka" %% "akka-actor" % AkkaVersion,
	"com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
	"com.typesafe.akka" %% "akka-stream" % AkkaVersion,
	"com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
	"com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)
