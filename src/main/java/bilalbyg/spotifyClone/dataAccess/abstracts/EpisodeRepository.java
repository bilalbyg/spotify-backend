package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Episode;

public interface EpisodeRepository extends JpaRepository<Episode, Integer>{

}
