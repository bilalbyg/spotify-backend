package bilalbyg.spotifyClone.business.concretes;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

import bilalbyg.spotifyClone.aws.bucket.BucketName;
import bilalbyg.spotifyClone.aws.filestore.FileStore;
import bilalbyg.spotifyClone.business.abstracts.PlaylistCoverImageService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.PlaylistCoverImageRepository;
import bilalbyg.spotifyClone.entities.concretes.PlaylistCoverImage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
public class PlaylistCoverImageManager implements PlaylistCoverImageService{

	private PlaylistCoverImageRepository playlistCoverImageRepository;
	private FileStore fileStore;
	
	@Override
	public List<PlaylistCoverImage> getAll() {
		// TODO Auto-generated method stub
		return playlistCoverImageRepository.findAll();
	}

	@Override
	public Result add(PlaylistCoverImage playlistCoverImage) {
		// TODO Auto-generated method stub
		playlistCoverImageRepository.save(playlistCoverImage);
		return new SuccessResult("Playlist cover image added");
	}

	@Override
	public Result update(PlaylistCoverImage playlistCoverImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void upload(int playlistId, MultipartFile file) {
		// TODO Auto-generated method stub
		
		// 1. Check if image is not empty
        isFileEmpty(file);
        
        // 2. If file is an image
        isImage(file);
        
        // 3. The playlist has a cover image already
        PlaylistCoverImage playlistCoverImage = getPlaylistCoverImageOrThrow(playlistId);
	
        // 4. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);
        
        // 5. Store the image in s3 and update database (playlistCoverImageLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PLAYLIST_COVER_IMAGE.getBucketName(), playlistCoverImage.getPlaylistId());
        String filename = String.format("%s-%s",UUID.randomUUID(), file.getOriginalFilename());

        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            playlistCoverImage.setPlaylistCoverImageLink(filename);
            playlistCoverImageRepository.save(playlistCoverImage);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
	}

//	@Override
//	public byte[] download(int playlistId) {
//		// TODO Auto-generated method stub
//		PlaylistCoverImage playlistCoverImage = getPlaylistCoverImageOrThrow(playlistId);
//		System.out.println(playlistId);
//        String path = String.format("%s/%s",
//                BucketName.PLAYLIST_COVER_IMAGE.getBucketName(),
//                playlistCoverImage.getPlaylistId());
//		System.out.println(path);
//
//        return playlistCoverImage.getPlaylistCoverImageLink()
//                .map(key -> fileStore.download(path, key))
//                .orElse(new byte[0]);
//
//	}
	
	@Override
	public String download(int playlistId) {
		// TODO Auto-generated method stub
		PlaylistCoverImage playlistCoverImage = getPlaylistCoverImageOrThrow(playlistId);
		String coverImageLink = playlistCoverImage.getPlaylistCoverImageLink().get();
        String path = String.format("https://%s.s3.eu-west-2.amazonaws.com/%s/%s",
                BucketName.PLAYLIST_COVER_IMAGE.getBucketName(),
                playlistCoverImage.getPlaylistId(), coverImageLink);
        System.out.println(path);
		return path;

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
    
    private PlaylistCoverImage getPlaylistCoverImageOrThrow(int playlistId) {
        return playlistCoverImageRepository
        		.findAll()
                .stream()
                .filter(playlistCoverImage -> playlistCoverImage.getPlaylistId() == playlistId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Playlist cover image %s not found", playlistId)));
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

	@Override
	public DataResult<PlaylistCoverImage> getPlaylistCoverImageByPlaylistId(int playlistId) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<PlaylistCoverImage>(playlistCoverImageRepository.getPlaylistCoverImageByPlaylistId(playlistId));
	}
}
