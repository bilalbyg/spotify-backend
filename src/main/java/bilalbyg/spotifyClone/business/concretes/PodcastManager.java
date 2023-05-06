package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.PodcastService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.PodcastRepository;
import bilalbyg.spotifyClone.entities.concretes.Podcast;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PodcastManager implements PodcastService{

	private PodcastRepository podcastRepository;
	
	@Override
	public DataResult<List<Podcast>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Podcast>>(podcastRepository.findAll(), "Podcasts listed") ;
	}

	@Override
	public Result add(Podcast podcast) {
		// TODO Auto-generated method stub
		podcastRepository.save(podcast);
		return new SuccessResult("Podcast added");
	}

	@Override
	public Result update(Podcast podcast) {
		// TODO Auto-generated method stub
		podcastRepository.save(podcast);
		return new SuccessResult("Podcast updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		podcastRepository.deleteById(id);
		return new SuccessResult();	
	}

}
