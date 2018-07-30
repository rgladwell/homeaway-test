package me.gladwell

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout

trait ListingRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[ListingRoutes])

  implicit lazy val timeout = Timeout(5.seconds)

  val listings: InMemoryListings

  lazy val listingRoutes: Route =
    pathPrefix("listings") {
      concat(
        post {
          entity(as[Listing]) { listing =>
            val id = listings.create(listing)
            redirect(s"/listings/$id", StatusCodes.PermanentRedirect)
          }
        },
        path(Segment) { id =>
          concat(
            get {
              val maybeListing = listings.findById(id)
              rejectEmptyResponse {
                complete(maybeListing)
              }
            },
            put {
              entity(as[Listing]) { listing =>
                val listingUpdated = listings.update(id, listing)
                onSuccess(listingUpdated) { listing =>
                  complete(StatusCodes.OK)
                }
              }
            },
            delete {
              val listingDeleted = listings.delete(id)
              onSuccess(listingDeleted) { listing =>
                complete(StatusCodes.OK)
              }
            })
        })
    }

}
