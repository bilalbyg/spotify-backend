package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.UserLikedArtistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserLikedArtistRepository;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserLikedArtistManager implements UserLikedArtistService{

	private UserLikedArtistRepository userLikedArtistRepository;
	
	@Override
	public DataResult<List<UserLikedArtist>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserLikedArtist>>(userLikedArtistRepository.findAll(), "All liked songs listed");
	}

	@Override
	public DataResult<UserLikedArtist> add(UserLikedArtist userLikedArtist) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedArtist>(userLikedArtistRepository.save(userLikedArtist),"Artist added to user liked artists.");
	}

	@Override
	public Result update(UserLikedArtist userLikedArtist) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		userLikedArtistRepository.deleteById(id);
		return new SuccessResult("Liked artist deleted");
	}
	
	@Override
	public DataResult<UserLikedArtist> getByUserIdAndArtistId(int userId, int artistId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedArtist>(userLikedArtistRepository.getByUserIdAndArtistId(userId, artistId));
	}
}
