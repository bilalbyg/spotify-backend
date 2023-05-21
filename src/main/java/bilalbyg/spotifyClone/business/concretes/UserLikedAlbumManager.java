package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.UserLikedAlbumService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserLikedAlbumRepository;
import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserLikedAlbumManager implements UserLikedAlbumService {

	private UserLikedAlbumRepository userLikedAlbumRepository;

	@Override
	public DataResult<List<UserLikedAlbum>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserLikedAlbum>>(userLikedAlbumRepository.findAll(),
				"All liked albums listed");
	}

	@Override
	public DataResult<UserLikedAlbum> add(UserLikedAlbum userLikedAlbum) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedAlbum>(userLikedAlbumRepository.save(userLikedAlbum),
				"Album added to user liked albums.");
	}

	@Override
	public Result update(UserLikedAlbum userLikedAlbum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		userLikedAlbumRepository.deleteById(id);
		return new SuccessResult("Liked album deleted");
	}

	@Override
	public DataResult<UserLikedAlbum> getByUserIdAndAlbumId(int userId, int albumId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedAlbum>(userLikedAlbumRepository.getByUserIdAndAlbumId(userId, albumId));
	}

}
