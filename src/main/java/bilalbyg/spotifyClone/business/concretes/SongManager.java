package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import bilalbyg.spotifyClone.business.abstracts.SongService;
import bilalbyg.spotifyClone.dataAccess.abstracts.SongRepository;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SongManager implements SongService{

	private SongRepository songRepository;
	
	@Override
	public List<Song> getAll() {
		// TODO Auto-generated method stub
		return this.songRepository.findAll();
	}

	@Override
	public void add(Song song) {
		// TODO Auto-generated method stub
		this.songRepository.save(song);
	}
}
