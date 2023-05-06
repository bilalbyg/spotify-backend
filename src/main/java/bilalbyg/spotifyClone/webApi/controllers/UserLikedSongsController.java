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

import bilalbyg.spotifyClone.business.abstracts.UserLikedSongService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedSong;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-liked-songs")
@AllArgsConstructor
@CrossOrigin
public class UserLikedSongsController {
	
	private UserLikedSongService userLikedSongService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody UserLikedSong userLikedSong) {
		return userLikedSongService.add(userLikedSong);
	}
	
	@GetMapping("/getall")
	public DataResult<List<UserLikedSong>> getAll(){
		return userLikedSongService.getAll();
	}
	
	@GetMapping("/getByUserIdAndSongId")
	public DataResult<UserLikedSong> getByUserIdAndSongId(@RequestParam int userId,@RequestParam int songId){
		return userLikedSongService.getByUserIdAndSongId(userId, songId);
	}
	
//	@PutMapping("/update")
//	public Result update(@RequestBody Episode episode) {
//		return episodeService.update(episode);
//	}
//	
	@DeleteMapping("/delete")
	public Result delete(@RequestParam int id) {
		return userLikedSongService.delete(id);
	}
}

