package dal

import sqlest._
import domain.{Artist, Album, AlbumType}
import dal.CdExtractors._
import dal.table._
import response._

trait AlbumDal extends SqlestDb {

  def getAlbums(searchTerm: String = "", artist: Option[Artist]): List[Album] = {

    val wildCardSearch = ("%" + searchTerm + "%").toUpperCase

    val baseQuery = 
      select
        .from(AlbumTable)
        .innerJoin(ArtistTable)
          .on(AlbumTable.artistId === ArtistTable.id)
        .leftJoin(ArtistLinkTable)
          .on(ArtistTable.id === ArtistLinkTable.relatedArtist)
        .leftJoin(SecondArtistTable)
          .on(ArtistLinkTable.parentArtist === SecondArtistTable.id)
        .innerJoin(AlbumTypeTable)
          .on(AlbumTable.albumType === AlbumTypeTable.albumType)
        .leftJoin(AlbumSideTable)
          .on(AlbumTable.id === AlbumSideTable.albumId)
        .where((upper(ArtistTable.displayName) like wildCardSearch) ||
               (upper(AlbumTable.name) like wildCardSearch) ||
               (upper(AlbumSideTable.sideName) like wildCardSearch)) 
        .orderBy(upper(AlbumTable.name), AlbumTable.id, AlbumSideTable.sideNumber)

    val finalQuery = if (artist.isEmpty) baseQuery
                     else {
                        baseQuery.where(AlbumTable.artistId === artist.get.id.get.constant)
                     }

    finalQuery.extractAll(albumExtractor)
   
  }

  /*def createAlbum(album: Album): ActionResponse[Album] = {        

    database.withTransaction { implicit transaction =>

      val insertAlbumStatement = 
        insert
          .into(AlbumTable)
          .values(AlbumTable.name -> album.name,
                  AlbumTable.artistId -> album.artist.id.get,
                  AlbumTable.releaseYear -> album.releaseYear,
                  AlbumTable.albumType -> album.albumType.code,
                  AlbumTable.lastPlayed -> album.lastPlayed,
                  AlbumTable.purchased -> album.purchased)
          .execute

      val newAlbumId = 
        select(max(AlbumTable.id))
          .from(AlbumTable)
          .fetchHead

      val insertSidesStatement = 
        if (insertAlbumStatement == 1 && !album.sides.isEmpty) {
          val baseQuery = 
            insert
              .into(AlbumSideTable)

            .values(AlbumSideTable.albumId -> album.id.get,
                    AlbumSideTable.sideNumber -> album.sides.index,
                    AlbumSideTable.sideName -> album.sides(index))
            .execute
        }
        else 1
        
      (insertAlbumStatement, newAlbumId, insertSidesStatement) match {
        case (1, y, 1) if (y.nonEmpty && y.get > 0) => ActionSuccess(album.copy(id = Some(y.get))) 
        case (x, _, _) if (x != 1) => Response.fail("Failed to create new album in database")
        case (_, y, _) if (y.isEmpty || y.get <= 0) => Response.fail("Album created in database but failed to retrieve new id")
        case _ => Response.fail("Album created in database but side records not created")
      }

    }  

  }*/

  /*def updateArtist(artist: Artist): ActionResponse[Artist] = {

    database.withTransaction { implicit transaction =>

      val updateStatement = 
        update(ArtistTable)
          .set(ArtistTable.displayName -> artist.displayName,
               ArtistTable.sortName -> artist.sortName)
          .where(ArtistTable.id === artist.id.get)
          .execute

      if (updateStatement == 1) ActionSuccess(artist)
      else Response.fail("Failed to update artist")
          
    }
  }

  def deleteArtist(artistId: Int): ActionResponse[Int] = {

    database.withTransaction { implicit transaction =>

      val deleteStatement = 
        delete
          .from(ArtistTable)
          .where(ArtistTable.id === artistId)
          .execute

      if (deleteStatement == 1) ActionSuccess(artistId)
      else Response.fail("Failed to delete artist")      
    }
  }*/

}

