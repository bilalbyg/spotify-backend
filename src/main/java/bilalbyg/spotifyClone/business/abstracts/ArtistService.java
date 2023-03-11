package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;
import bilalbyg.spotifyClone.entities.concretes.Artist;

public interface ArtistService {
	List<Artist> getAll();
	void add(Artist artist);
}
