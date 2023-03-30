package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import bilalbyg.spotifyClone.business.abstracts.SongService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.SongRepository;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SongManager implements SongService{

	private SongRepository songRepository;
	
	@Override
	public DataResult<List<Song>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Song>>(this.songRepository.findAll(), "Songs listed");
	}

	@Override
	public Result add(Song song) {
		// TODO Auto-generated method stub
		this.songRepository.save(song);
		return new SuccessResult("Song added");
	}

	@Override
	public Result update(Song song) {
		// TODO Auto-generated method stub
		this.songRepository.save(song);
		return new SuccessResult("Song updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		this.songRepository.deleteById(id);
		return new SuccessResult("Song deleted");
	}
}
