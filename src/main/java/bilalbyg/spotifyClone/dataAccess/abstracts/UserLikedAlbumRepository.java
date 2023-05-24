package bilalbyg.spotifyClone.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;

public interface UserLikedAlbumRepository extends JpaRepository<UserLikedAlbum, Integer> {
	List<UserLikedAlbum> getByUserId(int userId);
	UserLikedAlbum getByUserIdAndAlbumId(int userId, int albumId); 

}
