package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import bilalbyg.spotifyClone.business.abstracts.ArtistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.ArtistRepository;
import bilalbyg.spotifyClone.entities.concretes.Artist;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArtistManager implements ArtistService{

	private ArtistRepository artistRepository;
	
	@Override
	public DataResult<List<Artist>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Artist>>(this.artistRepository.findAll(), "All data listed");
	}

	@Override
	public Result add(Artist artist) {
		// TODO Auto-generated method stub
		this.artistRepository.save(artist);
		return new SuccessResult("Artist added");
	}

	@Override
	public DataResult<Artist> getByName(String name) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Artist>(this.artistRepository.getByName(name), "getByName succeed");
	}

	@Override
	public DataResult<Artist> getById(int id) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Artist>(this.artistRepository.getById(id), "getById succeed");
	}

	@Override
	public DataResult<List<Artist>> getByNameContains(String name) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Artist>>(this.artistRepository.getByNameContains(name), "getByNameContains succeed");
	}

	@Override
	public DataResult<List<Artist>> getByNameStartsWith(String name) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Artist>>(this.artistRepository.getByNameStartsWith(name), "getByNameStartsWith succeed");
	}

}
