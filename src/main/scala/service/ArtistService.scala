package service

import domain.Artist
import dal.ArtistDal
import com.typesafe.scalalogging._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.server.blaze._
import org.json4s._
import org.json4s.native.Serialization.read
import ServiceUtilities._

object ArtistService extends LazyLogging {

  val artistDal = new ArtistDal {}

  val artistService = HttpService {

    case GET -> Root / "artists" :? SearchTerm(searchTerm) =>
      logger.info(s"Received request artist / $searchTerm")
      httpJsonResponse(artistDal.getArtists(searchTerm))

    case GET -> Root / "artists" =>
      logger.info(s"Received request for all artists")
      httpJsonResponse(artistDal.getArtists())

    case GET -> Root / "artists" / artistId =>
      logger.info(s"Received request artist $artistId")
      httpJsonResponse(artistDal.getArtist(artistId.toInt))

    case req @ POST -> Root / "artists" =>
      req.decode[String] { data =>
        logger.info("Received artist create request for this: " + data)
        val a: Artist = read[Artist](data)
        val a2 = Artist.fixSortName(a)
        httpJsonResponse(artistDal.createArtist(a2))
      }

    case req @ PUT -> Root / "artists" / artistId =>
      req.decode[String] { data =>
        logger.info("Received artist update request for this: " + data)
        // TODO - some validation to make sure artistId from URI matches that from the body
        val a: Artist = read[Artist](data)
        val a2 = Artist.fixSortName(a)
        httpJsonResponse(artistDal.updateArtist(a2))
      }    

    case req @ DELETE -> Root / "artists" / artistId =>
      logger.info("Received artist delete request for this: " + artistId)
      // TODO - validation to ensure artistId is numeric
      httpJsonResponse(artistDal.deleteArtist(artistId.toInt))

  }

}
