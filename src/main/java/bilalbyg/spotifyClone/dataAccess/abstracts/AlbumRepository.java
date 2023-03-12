package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Album;


public interface AlbumRepository extends JpaRepository<Album, Integer>{
	Album getByName(String name);
	Album getById(int id);
	List<Album> getByNameContains(String name);
	List<Album> getByNameStartsWith(String name);
}
