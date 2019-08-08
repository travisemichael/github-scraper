package com.travisemichael

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object BaseRoutes {
  lazy val routes: Route = {
    pathEndOrSingleSlash {
      get {
        complete("\"Github Scraper v0\"")
      }
    } ~ path("ping") {
      get {
        complete("\"pong\"")
      }
    }
  }
}
