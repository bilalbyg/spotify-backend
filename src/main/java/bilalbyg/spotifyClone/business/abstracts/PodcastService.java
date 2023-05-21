package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Album;
import bilalbyg.spotifyClone.entities.concretes.Podcast;

public interface PodcastService {
	DataResult<List<Podcast>> getAll();
	DataResult<Podcast> getByPodcastId(int podcastId);
	DataResult<List<Podcast>> getPodcastsById(List<Integer> ids);
	Result add(Podcast podcast);
	Result update(Podcast podcast);
	Result delete(int id);
}
