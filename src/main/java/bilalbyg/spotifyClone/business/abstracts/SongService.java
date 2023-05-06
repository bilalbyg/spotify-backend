package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongService {
	DataResult<List<Song>> getAll();
	DataResult<Song> getById(int id);
	DataResult<List<Song>> getSongsById(List<Integer> ids);
	Result add(Song song);
	Result update(Song song);
	Result delete(int id);
}
