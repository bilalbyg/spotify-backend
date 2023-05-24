package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;

public interface UserLikedAlbumService {
	DataResult<List<UserLikedAlbum>> getAll();
	DataResult<UserLikedAlbum> getByUserIdAndAlbumId(int userId, int albumId);
	DataResult<UserLikedAlbum> add(UserLikedAlbum userLikedAlbum);
	Result update(UserLikedAlbum userLikedAlbum);
	Result delete(int id);
	
}
