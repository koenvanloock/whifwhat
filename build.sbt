name := """Whif-what"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
    ws,
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.6-play26",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
"org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


javaOptions in Test += "-Dconfig.resource=test.conf"