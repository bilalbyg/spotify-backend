package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongService {
	DataResult<List<Song>> getAll();
	Result add(Song song);
	Result update(Song song);
	Result delete(int id);
}
