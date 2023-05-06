package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Podcast;

public interface PodcastRepository extends JpaRepository<Podcast, Integer>{

}
