package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserEpisode;

public interface UserEpisodeService {
	DataResult<List<UserEpisode>> getAll();
	Result add(UserEpisode userEpisode);
	Result update(UserEpisode userEpisode);
	Result delete(int id);
}
