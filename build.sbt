name := """Whif-what"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.14",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.1",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

coverageExcludedPackages := "<empty>;Reverse.*;router.*;views.*"

addCommandAlias("scoverage", {
  "coverage test"
})

javaOptions in Test += "-Dconfig.resource=test.conf"