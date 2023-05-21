package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.EpisodeService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.EpisodeRepository;
import bilalbyg.spotifyClone.entities.concretes.Episode;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EpisodeManager implements EpisodeService{
	
	private EpisodeRepository episodeRepository;
	
	@Override
	public DataResult<List<Episode>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Episode>>(episodeRepository.findAll(), "Episodes listed");
	}

	@Override
	public Result add(Episode episode) {
		// TODO Auto-generated method stub
		episodeRepository.save(episode);
		return new SuccessResult("Episode added");
	}

	@Override
	public Result update(Episode episode) {
		// TODO Auto-generated method stub
		episodeRepository.save(episode);
		return new SuccessResult("Episode updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		episodeRepository.deleteById(id);
		return new SuccessResult();	
	}

	@Override
	public DataResult<Episode> getById(int id) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Episode>(episodeRepository.findById(id).orElse(null));
	}

	@Override
	public DataResult<List<Episode>> getEpisodesById(List<Integer> ids) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Episode>>(episodeRepository.findAllById(ids));
	}

	@Override
	public DataResult<List<Episode>> getByPodcastId(int podcastId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Episode>>(episodeRepository.getByPodcast_PodcastId(podcastId));
	}
}
