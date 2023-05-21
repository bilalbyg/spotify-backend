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

import bilalbyg.spotifyClone.business.abstracts.UserLikedArtistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedArtist;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-liked-artists")
@AllArgsConstructor
@CrossOrigin
public class UserLikedArtistsController {
	
	private UserLikedArtistService userLikedArtistService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public DataResult<UserLikedArtist> add(@RequestBody UserLikedArtist userLikedArtist) {
		return userLikedArtistService.add(userLikedArtist);
	}
	
	@GetMapping("/getall")
	public DataResult<List<UserLikedArtist>> getAll(){
		return userLikedArtistService.getAll();
	}
	
	@GetMapping("/getByUserIdAndArtistId")
	public DataResult<UserLikedArtist> getByUserIdAndArtistId(@RequestParam int userId,@RequestParam int artistId){
		return userLikedArtistService.getByUserIdAndArtistId(userId, artistId);
	}

	
//	@PutMapping("/update")
//	public Result update(@RequestBody Episode episode) {
//		return episodeService.update(episode);
//	}
//	
	@DeleteMapping("/delete")
	public Result delete(@RequestParam int id) {
		return userLikedArtistService.delete(id);
	}
}

