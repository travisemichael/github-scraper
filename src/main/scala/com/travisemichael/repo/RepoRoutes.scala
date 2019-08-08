package com.travisemichael.repo

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

class RepoRoutes(api: RepoApi) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  lazy val routes: Route = {
    pathPrefix("repos") {
      pathEndOrSingleSlash {
        get {
          complete(write(api.getAll.toArray))
        } ~
          post {
            parameters('start.as[Int], 'count.as[Int]) { (start, count) =>
              api.scrapeAll(start, count)
              complete(Option.empty[String])
            }
          }
      } ~
        path(IntNumber) { id =>
          get {
            rejectEmptyResponse {
              complete(api.get(id).map(_.toJson))
            }
          }
        } ~
        pathPrefix(Segment) { owner =>
          path(Segment) { name =>
            get {
              rejectEmptyResponse {
                complete(api.get(owner, name).map(_.toJson))
              }
            } ~
              post {
                rejectEmptyResponse {
                  complete(api.scrape(owner, name).map(_.toJson))
                }
              }
          }
        }
    }
  }
}

object RepoRoutes {
  def apply(): RepoRoutes = new RepoRoutes(RepoApi())
}
