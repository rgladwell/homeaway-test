package me.gladwell

import scala.concurrent.Future
import java.util.UUID.randomUUID

class InMemoryListings {

  private val listing =
    StoredListing(
      id = "5e22a83a-6f4f-11e6-8b77-86f30ca893d3",
      contact = Contact(
        phone = "15126841100",
        formattedPhone = "+1 512-684-1100"),
      address = Address(
        address = "1011 W 5th St",
        postalCode = "1011",
        countryCode = "US",
        city = "Austin",
        state = "TX",
        country = "United States"),
      location = Location(
        lat = 40.4255485534668,
        lng = -3.7075681686401367))

  private val listings = scala.collection.mutable.Map(listing.id -> listing)

  def findById(id: String): Future[Option[ListingResponse]] =
    Future.successful(listings.get(id).map { ListingResponse(_) })

  def create(listing: Listing): String = {
    val id = randomUUID().toString
    val storedListing =
      StoredListing(
        id = id,
        contact = listing.contact,
        address = listing.address,
        location = listing.location)

    listings += (id -> storedListing)
    id
  }

  def update(id: String, listing: Listing): Future[ListingResponse] = Future.successful {

    val storedListing =
      StoredListing(
        id = id,
        contact = listing.contact,
        address = listing.address,
        location = listing.location)

    listings.put(id, storedListing)
    ListingResponse(storedListing)
  }

  def delete(id: String): Future[Option[ListingResponse]] = Future.successful {
    val maybeListing = listings.get(id)
    for {
      listing <- maybeListing
    } yield {
      listings -= id
      ListingResponse(listing)
    }
  }

}
