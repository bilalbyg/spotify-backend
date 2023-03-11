package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongService {
	List<Song> getAll();
	void add(Song song);
}
