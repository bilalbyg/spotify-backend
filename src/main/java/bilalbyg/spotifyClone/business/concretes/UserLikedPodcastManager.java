package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.UserLikedAlbumService;
import bilalbyg.spotifyClone.business.abstracts.UserLikedPodcastService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserLikedAlbumRepository;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserLikedPodcastRepository;
import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserLikedPodcastManager implements UserLikedPodcastService {

	private UserLikedPodcastRepository userLikedPodcastRepository;

	@Override
	public DataResult<List<UserLikedPodcast>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserLikedPodcast>>(userLikedPodcastRepository.findAll(),
				"All liked podcasts listed");
	}

	@Override
	public DataResult<UserLikedPodcast> add(UserLikedPodcast userLikedPodcast) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedPodcast>(userLikedPodcastRepository.save(userLikedPodcast),
				"Podcast added to user liked podcasts.");
	}

	@Override
	public Result update(UserLikedPodcast userLikedPodcast) {
		// TODO Auto-generated method stub
		userLikedPodcastRepository.save(userLikedPodcast);
		return new SuccessResult("User liked podcast updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		userLikedPodcastRepository.deleteById(id);
		return new SuccessResult("Liked podcast deleted");
	}

	@Override
	public DataResult<UserLikedPodcast> getByUserIdAndPodcastId(int userId, int podcastId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserLikedPodcast>(userLikedPodcastRepository.getByUserIdAndPodcastId(userId, podcastId));
	}

}
