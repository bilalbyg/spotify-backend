package bilalbyg.spotifyClone.business.concretes;

import static org.apache.http.entity.ContentType.IMAGE_GIF;
import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import bilalbyg.spotifyClone.aws.bucket.BucketName;
import bilalbyg.spotifyClone.aws.filestore.FileStore;
import bilalbyg.spotifyClone.business.abstracts.PlaylistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.PlaylistRepository;
import bilalbyg.spotifyClone.entities.concretes.Playlist;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
@Transactional
public class PlaylistManager implements PlaylistService{

	private PlaylistRepository playlistRepository;
	private FileStore fileStore;
	
	@Override
	public DataResult<List<Playlist>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Playlist>>(playlistRepository.findAll(), "Playlists listed");
	}

	@Override
	public DataResult<Playlist> add(Playlist playlist) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Playlist>(playlistRepository.save(playlist),"Playlist added");
	}

	@Override
	public Result update(Playlist playlist) {
		// TODO Auto-generated method stub
		playlistRepository.save(playlist);
		return new SuccessResult("Playlist updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		playlistRepository.deleteById(id);
		return new SuccessResult("Playlist deleted");
	}

	@Override
	public DataResult<Playlist> getById(int id) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Playlist>(playlistRepository.findById(id).orElse(null), "Playlist listed");
	}

	@Override
	public DataResult<List<Playlist>> getByPlaylistUserId(int playlistUserId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Playlist>>(playlistRepository.getByPlaylistUserId(playlistUserId));
	}

	@Override
	public DataResult<Playlist> getByPlaylistName(String playlistName) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<Playlist>(playlistRepository.getByPlaylistName(playlistName));
	}

	@Override
	public void updatePlaylistCoverImageUrl(int playlistId, String playlistCoverImageUrl) {
		// TODO Auto-generated method stub
		playlistRepository.updatePlaylistCoverImageUrl(playlistId, playlistCoverImageUrl);
	}

	@Override
	public DataResult<Playlist> uploadPlaylistCoverImageAndUpdate(int playlistId, MultipartFile file) {
		// TODO Auto-generated method stub
		
		// 1. Check if image is not empty
        isFileEmpty(file);
        
        // 2. If file is an image
        isImage(file);
        
        // 3. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);
        
        // 4. Store the image in s3 and update database (playlistCoverImageLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PLAYLIST_COVER_IMAGE.getBucketName(), playlistId);
        String filename = String.format("%s-%s",UUID.randomUUID(), file.getOriginalFilename());
        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            String playlistCoverImageUrl = "https://spotify-clone-123.s3.eu-west-2.amazonaws.com/" + playlistId + "/" + filename;
            updatePlaylistCoverImageUrl(playlistId, playlistCoverImageUrl);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
		return new SuccessDataResult<Playlist>(playlistRepository.findById(playlistId).orElse(null));
	}
	
    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [ " + file.getSize() + "]");
        }
    }
    
    private void isImage(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }
    
    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }
}
