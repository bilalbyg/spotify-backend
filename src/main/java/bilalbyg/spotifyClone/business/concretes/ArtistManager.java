package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;
import bilalbyg.spotifyClone.business.abstracts.ArtistService;
import bilalbyg.spotifyClone.dataAccess.abstracts.ArtistRepository;
import bilalbyg.spotifyClone.entities.concretes.Artist;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ArtistManager implements ArtistService{

	private ArtistRepository artistRepository;
	
	@Override
	public List<Artist> getAll() {
		// TODO Auto-generated method stub
		return this.artistRepository.findAll();
	}

	@Override
	public void add(Artist artist) {
		// TODO Auto-generated method stub
		this.artistRepository.save(artist);
	}

}
