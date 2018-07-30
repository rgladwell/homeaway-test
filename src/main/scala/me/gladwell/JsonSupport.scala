package me.gladwell

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val contactJsonFormat = jsonFormat2(Contact)
  implicit val addressJsonFormat = jsonFormat6(Address)
  implicit val locationJsonFormat = jsonFormat2(Location)
  implicit val listingJsonFormat = jsonFormat3(Listing)
  implicit val storedListingJsonFormat = jsonFormat4(StoredListing)
  implicit val listingResponseJsonFormat = jsonFormat1(ListingResponse)

}
