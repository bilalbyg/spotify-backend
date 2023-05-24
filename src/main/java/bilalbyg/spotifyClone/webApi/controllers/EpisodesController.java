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
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Episode;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/episodes")
@AllArgsConstructor
@CrossOrigin
public class EpisodesController {
	
	private EpisodeService episodeService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Episode episode) {
		return episodeService.add(episode);
	}
	
	@GetMapping("/getall")
	public DataResult<List<Episode>> getAll(){
		return episodeService.getAll();
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Episode episode) {
		return episodeService.update(episode);
	}
	
	@DeleteMapping("/delete")
	public Result update(@RequestParam int id) {
		return episodeService.delete(id);
	}
	
	@GetMapping("/getById")
	public DataResult<Episode> getById(@RequestParam int id){
		return episodeService.getById(id);
	}
	
	@GetMapping("/getEpisodesById")
	public DataResult<List<Episode>> getEpisodesById(@RequestParam List<Integer> ids){
		return episodeService.getEpisodesById(ids);
	}
	
	@GetMapping("/getByPodcastId")
	public DataResult<List<Episode>> getByPodcastId(@RequestParam int podcastId){
		return episodeService.getByPodcastId(podcastId);
	}
}
