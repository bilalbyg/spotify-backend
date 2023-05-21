package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;

public interface UserLikedSongService {
	DataResult<List<UserLikedSong>> getAll();
	DataResult<List<UserLikedSong>> getByUserId(int userId);
	DataResult<UserLikedSong> getByUserIdAndSongId(int userId, int songId);
	Result add(UserLikedSong userLikedSong);
	Result update(UserLikedSong userLikedSong);
	Result delete(int id);
	
}
