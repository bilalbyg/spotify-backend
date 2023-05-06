package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Playlist;

public interface PlaylistService {
	DataResult<List<Playlist>> getAll();
	DataResult<Playlist> getById(int id);
	DataResult<List<Playlist>> getByPlaylistUserId(int playlistUserId);
	DataResult<Playlist> getByPlaylistName(String playlistName);
	void updatePlaylistCoverImageUrl(int playlistId, String playlistCoverImageUrl);
	DataResult<Playlist> add(Playlist playlist);
	Result update(Playlist playlist);
	Result delete(int id);
	DataResult<Playlist> uploadPlaylistCoverImageAndUpdate(int playlistId, MultipartFile file);
}
