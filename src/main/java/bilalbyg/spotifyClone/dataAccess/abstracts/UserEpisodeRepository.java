package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserEpisode;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;

public interface UserEpisodeRepository extends JpaRepository<UserEpisode, Integer>{
	List<UserEpisode> getByUserId(int userId);
	UserEpisode getByUserIdAndEpisodeId(int userId, int episodeId); 
}
