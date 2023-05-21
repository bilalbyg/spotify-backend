package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bilalbyg.spotifyClone.business.abstracts.PlaylistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Playlist;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/playlists")
@AllArgsConstructor
@CrossOrigin
public class PlaylistsController {
	
	private PlaylistService playlistService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public DataResult<Playlist> add(@RequestBody Playlist playlist) {
		return playlistService.add(playlist);

	}
	
	@GetMapping("/getall")
	public DataResult<List<Playlist>> getAll(){
		return playlistService.getAll();
	}
	
	@GetMapping("/getById")
	public DataResult<Playlist> getById(@RequestParam int id){
		return playlistService.getById(id);
	}
	
	@GetMapping("/getByPlaylistUserId")
	public DataResult<List<Playlist>> getByPlaylistUserId(@RequestParam int playlistUserId){
		return playlistService.getByPlaylistUserId(playlistUserId);
	}
	
	@GetMapping("/getByPlaylistName")
	public DataResult<Playlist> getByPlaylistUserId(@RequestParam String playlistName){
		return playlistService.getByPlaylistName(playlistName);
	}
	
	@PutMapping("/update")
	public DataResult<Playlist> update(@RequestBody Playlist playlist) {
		return playlistService.update(playlist);
	}
	
	@PostMapping("/updatePlaylistCoverImageUrl")
	public void updatePlaylistCoverImageUrl(@RequestParam int playlistId, @RequestParam String playlistCoverImageUrl) {
		playlistService.updatePlaylistCoverImageUrl(playlistId, playlistCoverImageUrl);
	}
	
    @PostMapping(
            path = "/uploadImage/{playlistId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public DataResult<Playlist> upload(@PathVariable("playlistId") int playlistId,
                       @RequestParam("file") MultipartFile file) {
    	return playlistService.uploadPlaylistCoverImageAndUpdate(playlistId, file);
    }
    
	@DeleteMapping("/delete")
	public Result delete(@RequestParam int playlistId) {
		return playlistService.delete(playlistId);
	}
}