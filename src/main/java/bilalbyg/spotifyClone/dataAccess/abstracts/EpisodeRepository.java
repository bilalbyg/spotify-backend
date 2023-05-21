package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Integer>{
	List<Episode> getByPodcast_PodcastId(int podcastId);
}
