package com.travisemichael.user

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

class UserRoutes(api: UserApi) {
  implicit val formats: DefaultFormats.type = DefaultFormats

  lazy val routes: Route = {
    pathPrefix("users") {
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
        path(Segment) { login =>
          get {
            rejectEmptyResponse {
              complete(api.get(login).map(_.toJson))
            }
          } ~
            post {
              rejectEmptyResponse {
                complete(api.scrape(login).map(_.toJson))
              }
            }
        }
    }
  }
}

object UserRoutes {
  def apply(): UserRoutes = new UserRoutes(UserApi())
}
