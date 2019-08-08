name := "github-scraper"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Mesosphere" at "http://downloads.mesosphere.io/maven"

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.17",
  "org.json4s" %% "json4s-native" % "3.6.7",
  "org.apache.httpcomponents" % "httpclient" % "4.5.6",
  "org.quartz-scheduler" % "quartz" % "2.3.1",
  "org.slf4j" % "slf4j-simple" % "1.7.26",
  "com.typesafe.akka" %% "akka-http" % "10.1.9",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23",
  "io.spray" %% "spray-json" % "1.3.5"
)

enablePlugins(JavaAppPackaging)
mainClass in Compile := Some("com.travisemichael.Main")
dockerExposedPorts := Seq(8181)

mappings in Universal += file("scrape.cron") -> "scrape.cron"
mappings in Universal += file("users.json") -> "users.json"
mappings in Universal += file("repos.json") -> "repos.json"
