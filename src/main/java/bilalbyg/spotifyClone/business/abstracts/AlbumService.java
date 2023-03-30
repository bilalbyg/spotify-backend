package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Album;

public interface AlbumService {
	DataResult<List<Album>> getAll();
	Result add(Album album);
	Result update(Album album);
	Result delete(int id);
}
