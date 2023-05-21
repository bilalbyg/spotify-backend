package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Album;


public interface AlbumRepository extends JpaRepository<Album, Integer>{
	Album getByAlbumName(String name);
	Album getById(int id);
	List<Album> getByArtist_ArtistId(int artistId);
	List<Album> getByAlbumNameContains(String name);
	List<Album> getByAlbumNameStartsWith(String name);
}
