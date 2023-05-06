package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.UserLikedSongService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserLikedSongRepository;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserLikedSongManager implements UserLikedSongService{

	private UserLikedSongRepository userLikedSongRepository;
	
	@Override
	public DataResult<List<UserLikedSong>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserLikedSong>>(userLikedSongRepository.findAll(), "All liked songs listed");
	}

	@Override
	public Result add(UserLikedSong userLikedSong) {
		// TODO Auto-generated method stub
		userLikedSongRepository.save(userLikedSong);
		return new SuccessResult("Song added to user liked song.");
	}

	@Override
	public Result update(UserLikedSong userLikedSong) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		userLikedSongRepository.deleteById(id);
		return new SuccessResult("Liked song deleted");
	}

	@Override
	public DataResult<UserLikedSong> getByUserIdAndSongId(int userId, int songId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedSong>(userLikedSongRepository.getByUserIdAndSongId(userId, songId));
	}

}
