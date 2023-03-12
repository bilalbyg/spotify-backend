package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Artist;

public interface ArtistService {
	DataResult<List<Artist>> getAll();
	Result add(Artist artist);
	DataResult<Artist> getByName(String name);
	DataResult<Artist> getById(int id);
	DataResult<List<Artist>> getByNameContains(String name);
	DataResult<List<Artist>> getByNameStartsWith(String name);
}
