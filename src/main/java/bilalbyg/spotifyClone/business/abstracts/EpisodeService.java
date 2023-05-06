package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Episode;

public interface EpisodeService {
	DataResult<List<Episode>> getAll();
	DataResult<Episode> getById(int id);
	DataResult<List<Episode>> getEpisodesById(List<Integer> ids);
	Result add(Episode episode);
	Result update(Episode episode);
	Result delete(int id);
}
