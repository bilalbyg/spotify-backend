package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;

public interface UserLikedSongRepository extends JpaRepository<UserLikedSong, Integer>{
	List<UserLikedSong> getByUserId(int userId);
	UserLikedSong getByUserIdAndSongId(int userId, int songId);
}
