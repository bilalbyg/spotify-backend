package bilalbyg.spotifyClone.webApi.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import bilalbyg.spotifyClone.business.abstracts.PlaylistCoverImageService;
import bilalbyg.spotifyClone.business.abstracts.PodcastService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.PlaylistCoverImage;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/playlist-cover-images")
@AllArgsConstructor
@CrossOrigin("*")
public class PlaylistCoverImagesController {

    private PlaylistCoverImageService playlistCoverImageService;

	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody PlaylistCoverImage playlistCoverImage) {
		return playlistCoverImageService.add(playlistCoverImage);
	}
    
    @GetMapping("/getall")
    public List<PlaylistCoverImage> getAll() {
        return playlistCoverImageService.getAll();
    }

    @PostMapping(
            path = "/upload/{playlistId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void upload(@PathVariable("playlistId") int playlistId,
                                       @RequestParam("file") MultipartFile file) {
    	playlistCoverImageService.upload(playlistId, file);
    }

    @GetMapping("/download/{playlistId}")
    public String download(@PathVariable("playlistId") int playlistId) {
        return playlistCoverImageService.download(playlistId);
    }
    
    @GetMapping("/getByPlaylistId")
    public DataResult<PlaylistCoverImage> getByPlaylistId(@RequestParam int playlistId){
    	return playlistCoverImageService.getPlaylistCoverImageByPlaylistId(playlistId);
    }

}
