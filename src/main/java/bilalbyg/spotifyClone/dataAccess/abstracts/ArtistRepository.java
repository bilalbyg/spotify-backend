package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Integer>{
	// specified queries
}
