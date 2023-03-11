package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.AlbumService;
import bilalbyg.spotifyClone.dataAccess.abstracts.AlbumRepository;
import bilalbyg.spotifyClone.entities.concretes.Album;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlbumManager implements AlbumService{

	private AlbumRepository albumRepository;
	
	@Override
	public List<Album> getAll() {
		// TODO Auto-generated method stub
		return this.albumRepository.findAll();
	}

	@Override
	public void add(Album album) {
		// TODO Auto-generated method stub
		this.albumRepository.save(album);
	}

}
