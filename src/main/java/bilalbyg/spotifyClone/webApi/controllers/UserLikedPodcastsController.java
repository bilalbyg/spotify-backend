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

import bilalbyg.spotifyClone.business.abstracts.UserLikedPodcastService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.UserLikedPodcast;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/user-liked-podcasts")
@AllArgsConstructor
@CrossOrigin
public class UserLikedPodcastsController {
	
	private UserLikedPodcastService userLikedPodcastService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public DataResult<UserLikedPodcast> add(@RequestBody UserLikedPodcast userLikedPodcast) {
		return userLikedPodcastService.add(userLikedPodcast);
	}
	
	@GetMapping("/getall")
	public DataResult<List<UserLikedPodcast>> getAll(){
		return userLikedPodcastService.getAll();
	}
	
	@DeleteMapping("/delete")
	public Result delete(@RequestParam int id) {
		return userLikedPodcastService.delete(id);
	}
	
	@GetMapping("/getByUserIdAndPodcastId")
	public DataResult<UserLikedPodcast> getByUserIdAndPodcastId(@RequestParam int userId,@RequestParam int podcastId){
		return userLikedPodcastService.getByUserIdAndPodcastId(userId, podcastId);
	}
}

