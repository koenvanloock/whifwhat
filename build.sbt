name := """Whif-what"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.39",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

coverageExcludedPackages := "<empty>;Reverse.*;router.*;views.*"

addCommandAlias("scoverage", {
  "clean coverage test"; "coverageReport"
})