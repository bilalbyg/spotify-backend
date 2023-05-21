package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;

public interface UserLikedPodcastRepository extends JpaRepository<UserLikedPodcast, Integer> {
	List<UserLikedPodcast> getByUserId(int userId);
	UserLikedPodcast getByUserIdAndPodcastId(int userId, int podcastId); 

}
