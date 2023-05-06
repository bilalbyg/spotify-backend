package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserEpisode;

public interface UserEpisodeRepository extends JpaRepository<UserEpisode, Integer>{
	List<UserEpisode> getByUserId(int userId);
}
