package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.PlaylistCoverImage;

public interface PlaylistCoverImageRepository extends JpaRepository<PlaylistCoverImage, Integer>{
	PlaylistCoverImage getPlaylistCoverImageByPlaylistId(int playlistId); 
}
