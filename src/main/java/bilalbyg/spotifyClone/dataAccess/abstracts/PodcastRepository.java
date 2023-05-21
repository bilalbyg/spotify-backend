package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Podcast;
import bilalbyg.spotifyClone.entities.concretes.Song;

public interface PodcastRepository extends JpaRepository<Podcast, Integer>{
	Podcast getByPodcastId(int podcastId);

}
