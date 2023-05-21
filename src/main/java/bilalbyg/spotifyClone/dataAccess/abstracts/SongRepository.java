package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongRepository extends JpaRepository<Song,Integer>{
	Song getBySongName(String songName);
	Song getById(int id);
	List<Song> getBySongNameContains(String name);
	List<Song> getBySongNameStartsWith(String name);
	List<Song> getByAlbum_AlbumId(int albumId);
	List<Song> getByCategory_CategoryId(int categoryId);
}
