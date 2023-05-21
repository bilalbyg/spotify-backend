package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bilalbyg.spotifyClone.business.abstracts.EpisodeService;
import bilalbyg.spotifyClone.business.abstracts.UserEpisodeService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Episode;
import bilalbyg.spotifyClone.entities.concretes.Playlist;
import bilalbyg.spotifyClone.entities.concretes.UserEpisode;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-episodes")
@AllArgsConstructor
@CrossOrigin
public class UserEpisodesController {
	
	private UserEpisodeService userEpisodeService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public DataResult<UserEpisode> add(@RequestBody UserEpisode userEpisode) {
		return userEpisodeService.add(userEpisode);
	}
	
	@GetMapping("/getall")
	public DataResult<List<UserEpisode>> getAll(){
		return userEpisodeService.getAll();
	}
	
	@GetMapping("/getByUserIdAndEpisodeId")
	public DataResult<UserEpisode> getByUserIdAndEpisodeId(@RequestParam int userId,@RequestParam int episodeId){
		return userEpisodeService.getByUserIdAndEpisodeId(userId, episodeId);
	}
	
	@GetMapping("/getByUserId")
	public DataResult<List<UserEpisode>> getByPlaylistUserId(@RequestParam int userId){
		return userEpisodeService.getByUserId(userId);
	}
	
//	@PutMapping("/update")
//	public Result update(@RequestBody Episode episode) {
//		return episodeService.update(episode);
//	}
//	
	@DeleteMapping("/delete")
	public Result update(@RequestParam int id) {
		return userEpisodeService.delete(id);
	}
}

