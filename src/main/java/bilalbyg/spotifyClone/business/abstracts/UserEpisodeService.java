package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserEpisode;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;

public interface UserEpisodeService {
	DataResult<List<UserEpisode>> getAll();
	DataResult<UserEpisode> add(UserEpisode userEpisode);
	DataResult<List<UserEpisode>>getByUserId(int userId);
	DataResult<UserEpisode> getByUserIdAndEpisodeId(int userId, int episodeId); 
	Result update(UserEpisode userEpisode);
	Result delete(int id);
}
