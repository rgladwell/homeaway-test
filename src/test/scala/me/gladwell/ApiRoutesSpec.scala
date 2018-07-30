package me.gladwell

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.server._

class ApiRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with ListingRoutes {

  lazy val routes = listingRoutes

  override val listings = new InMemoryListings

  "API" should {

    "reject non-existant listing" in {
      val request = HttpRequest(uri = "/listings/does-not-exist")

      request ~> Route.seal(routes) ~> check {
        status should ===(StatusCodes.NotFound)
      }
    }

    "return listing" in {
      val request = HttpRequest(uri = "/listings/5e22a83a-6f4f-11e6-8b77-86f30ca893d3")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===(
          """{"listing":{"id":"5e22a83a-6f4f-11e6-8b77-86f30ca893d3","contact":{"phone":"15126841100","formattedPhone":"+1 512-684-1100"},"address":{"city":"Austin","state":"TX","country":"United States","postalCode":"1011","countryCode":"US","address":"1011 W 5th St"},"location":{"lat":40.4255485534668,"lng":-3.7075681686401367}}}""")
      }
    }

    "add new listings" in {
      val listingEntity = """{
        "contact": {
          "phone": "02086432392",
          "formattedPhone": "+44 208-643-2392"
        },
        "address": {
          "address": "9 South Drive",
          "postalCode": "SM2 7PH",
          "countryCode": "UK",
          "city": "Cheam",
          "state": "London",
          "country": "United Kingdoms"
        },
        "location": {
          "lat": 51.3317246,
          "lng": -0.1793822
        }
      }"""

      val request = Post("/listings").withEntity(ContentTypes.`application/json`, listingEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.PermanentRedirect)
        header("Location") match {
          case Some(url) => url.value() should startWith("/listings/")
          case _ => fail()
        }
      }
    }

    "update listings" in {
      val listingEntity = """{
        "contact": {
          "phone": "02086432393",
          "formattedPhone": "+44 208-643-2393"
        },
        "address": {
          "address": "172 Revelstoke Road",
          "postalCode": "SW18 5{A",
          "countryCode": "UK",
          "city": "Southfields",
          "state": "London",
          "country": "United Kingdoms"
        },
        "location": {
          "lat": 51.3317246,
          "lng": -0.1793822
        }
      }"""

      val request = Put(uri = "/listings/5e22a83a-6f4f-11e6-8b77-86f30ca893d3").withEntity(ContentTypes.`application/json`, listingEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
      }
    }

    "remove listings" in {
      val request = Delete(uri = "/listings/5e22a83a-6f4f-11e6-8b77-86f30ca893d3")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
      }
    }

  }

}
