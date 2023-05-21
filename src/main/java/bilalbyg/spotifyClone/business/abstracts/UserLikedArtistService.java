package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;

public interface UserLikedArtistService {
	DataResult<List<UserLikedArtist>> getAll();
	DataResult<UserLikedArtist> getByUserIdAndArtistId(int userId, int artistId);
	DataResult<UserLikedArtist> add(UserLikedArtist userLikedArtist);
	Result update(UserLikedArtist userLikedArtist);
	Result delete(int id);
	
}
