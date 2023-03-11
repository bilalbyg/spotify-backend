package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.Song;

public interface SongRepository extends JpaRepository<Song,Integer>{

}
