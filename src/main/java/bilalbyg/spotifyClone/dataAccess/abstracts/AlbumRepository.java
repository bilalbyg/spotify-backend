package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import bilalbyg.spotifyClone.entities.concretes.Album;

public interface AlbumRepository extends JpaRepository<Album, Integer>{

}
