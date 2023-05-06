package bilalbyg.spotifyClone.dataAccess.abstracts;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import bilalbyg.spotifyClone.entities.concretes.Playlist;


public interface PlaylistRepository extends JpaRepository<Playlist, Integer>{
	List<Playlist> getByPlaylistUserId(int playlistUserId);
	Playlist getByPlaylistName(String playlistName);
	
	@Modifying
	@Query("Update Playlist p set p.playlistCoverImageUrl=:playlistCoverImageUrl where p.playlistId=:playlistId")
	void updatePlaylistCoverImageUrl(int playlistId, String playlistCoverImageUrl);
}
