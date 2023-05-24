package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.AlbumService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.AlbumRepository;
import bilalbyg.spotifyClone.entities.concretes.Album;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlbumManager implements AlbumService{

	private AlbumRepository albumRepository;
	
	@Override
	public DataResult<List<Album>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Album>>(this.albumRepository.findAll(), "All albums listed");
	}

	@Override
	public Result add(Album album) {
		// TODO Auto-generated method stub
		this.albumRepository.save(album);
		return new SuccessResult("Album added");
	}

	@Override
	public Result update(Album album) {
		// TODO Auto-generated method stub
		this.albumRepository.save(album);
		return new SuccessResult("Album updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		this.albumRepository.deleteById(id);
		return new SuccessResult("Album deleted");
	}

	@Override
	public DataResult<Album> getByAlbumId(int albumId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Album>(albumRepository.findById(albumId).orElse(null));
	}

	@Override
	public DataResult<List<Album>> getByArtistId(int artistId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Album>>(albumRepository.getByArtist_ArtistId(artistId));
	}

	@Override
	public DataResult<List<Album>> getAlbumsById(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Album>>(albumRepository.findAllById(ids));
	}

}
