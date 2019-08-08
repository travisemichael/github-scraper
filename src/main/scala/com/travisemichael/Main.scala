package com.travisemichael

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.travisemichael.repo.{RepoRoutes, RepoService}
import com.travisemichael.user.{UserRoutes, UserService}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object Main extends App {
  private val logger = LoggerFactory.getLogger(getClass)

  // Setup for akka-http server
  implicit val system: ActorSystem = ActorSystem("actor-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  logger.info("Creating database tables...")
  UserService().createTableIfNotExists()
  RepoService().createTableIfNotExists()

  private val routes = BaseRoutes.routes ~ UserRoutes().routes ~ RepoRoutes().routes
  private val port = sys.env.getOrElse("GITHUB_SCRAPER_PORT", "8181").toInt
  Http().bindAndHandle(routes, "0.0.0.0", port)
  logger.info(s"Listening on port $port...")

  Quartz.scheduleJobs()
  StdIn.readLine()
}
