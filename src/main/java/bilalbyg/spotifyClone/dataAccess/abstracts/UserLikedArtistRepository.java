package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;

public interface UserLikedArtistRepository extends JpaRepository<UserLikedArtist, Integer>{
	List<UserLikedArtist> getByUserId(int userId);
	UserLikedArtist getByUserIdAndArtistId(int userId, int artistId); 
}
