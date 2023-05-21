package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;

public interface UserLikedPodcastService {
	DataResult<List<UserLikedPodcast>> getAll();
	DataResult<UserLikedPodcast> getByUserIdAndPodcastId(int userId, int podcastId);
	DataResult<UserLikedPodcast> add(UserLikedPodcast userLikedPodcast);
	Result update(UserLikedPodcast userLikedPodcast);
	Result delete(int id);
	
}
