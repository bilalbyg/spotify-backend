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

import bilalbyg.spotifyClone.business.abstracts.PodcastService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Podcast;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/podcasts")
@AllArgsConstructor
@CrossOrigin
public class PodcastsController {
	
	private PodcastService podcastService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Podcast podcast) {
		return podcastService.add(podcast);
	}
	
	@GetMapping("/getall")
	public DataResult<List<Podcast>> getAll(){
		return podcastService.getAll();
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Podcast podcast) {
		return podcastService.update(podcast);
	}
	
	@DeleteMapping("/delete")
	public Result update(@RequestParam int id) {
		return podcastService.delete(id);
	}
}
