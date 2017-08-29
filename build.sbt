name := """play-scala-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.h2database" % "h2" % "1.4.194"
)

javaOptions in run += "-Dlogback.debug=true"
