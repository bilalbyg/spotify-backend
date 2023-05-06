package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.PlaylistCoverImage;

public interface PlaylistCoverImageService {
	List<PlaylistCoverImage> getAll();
	Result add(PlaylistCoverImage playlistCoverImage);
	Result update(PlaylistCoverImage playlistCoverImage);
	Result delete(int id);
	DataResult<PlaylistCoverImage> getPlaylistCoverImageByPlaylistId(int playlistId);
	void upload(int playlistId, MultipartFile file);
//	byte[] download(int playlistId);
	String download(int playlistId);
}
