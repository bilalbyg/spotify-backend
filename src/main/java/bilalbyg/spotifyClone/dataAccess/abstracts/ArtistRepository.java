package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Integer>{
	// specified queries
	Artist getByName(String name);
	Artist getById(int id);
	List<Artist> getByNameContains(String name);
	List<Artist> getByNameStartsWith(String name);
}
