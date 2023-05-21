package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.UserEpisodeService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.UserEpisodeRepository;
import bilalbyg.spotifyClone.entities.concretes.UserEpisode;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserEpisodeManager implements UserEpisodeService{

	private UserEpisodeRepository userEpisodeRepository;
	
	@Override
	public DataResult<List<UserEpisode>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserEpisode>>(userEpisodeRepository.findAll()); 
	}

	@Override
	public DataResult<UserEpisode> add(UserEpisode userEpisode) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserEpisode>(userEpisodeRepository.save(userEpisode), "Episode added to user episodes."); 
	}

	@Override
	public Result update(UserEpisode userEpisode) {
		// TODO Auto-generated method stub
		userEpisodeRepository.save(userEpisode);
		return new SuccessResult("User episode updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		userEpisodeRepository.deleteById(id);
		return new SuccessResult("User episode deleted");
	}

	@Override
	public DataResult<List<UserEpisode>> getByUserId(int userId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<UserEpisode>>(userEpisodeRepository.getByUserId(userId));
	}

	@Override
	public DataResult<UserEpisode> getByUserIdAndEpisodeId(int userId, int episodeId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<UserEpisode>(userEpisodeRepository.getByUserIdAndEpisodeId(userId, episodeId));
	}

}
