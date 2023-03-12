package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongRepository extends JpaRepository<Song,Integer>{
	Song getByName(String name);
	Song getById(int id);
	List<Song> getByNameContains(String name);
	List<Song> getByNameStartsWith(String name);
}
