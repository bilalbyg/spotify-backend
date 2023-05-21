package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bilalbyg.spotifyClone.business.abstracts.UserLikedAlbumService;
import bilalbyg.spotifyClone.business.abstracts.UserLikedSongService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedAlbum;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-liked-albums")
@AllArgsConstructor
@CrossOrigin
public class UserLikedAlbumsController {
	
	private UserLikedAlbumService userLikedAlbumService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public DataResult<UserLikedAlbum> add(@RequestBody UserLikedAlbum userLikedAlbum) {
		return userLikedAlbumService.add(userLikedAlbum);
	}
	
	@GetMapping("/getall")
	public DataResult<List<UserLikedAlbum>> getAll(){
		return userLikedAlbumService.getAll();
	}
	
	
//	@PutMapping("/update")
//	public Result update(@RequestBody Episode episode) {
//		return episodeService.update(episode);
//	}
//	
	@DeleteMapping("/delete")
	public Result delete(@RequestParam int id) {
		return userLikedAlbumService.delete(id);
	}
	
	@GetMapping("/getByUserIdAndAlbumId")
	public DataResult<UserLikedAlbum> getByUserIdAndAlbumId(@RequestParam int userId,@RequestParam int albumId){
		return userLikedAlbumService.getByUserIdAndAlbumId(userId, albumId);
	}
}

