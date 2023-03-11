package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import bilalbyg.spotifyClone.business.abstracts.ArtistService;
import bilalbyg.spotifyClone.entities.concretes.Artist;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/artists")
public class ArtistsController {
	
	private ArtistService artistService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public void add(@RequestBody Artist artist) {
		this.artistService.add(artist);
	}
	
	@GetMapping("/getall")
	public List<Artist> getAll(){
		return this.artistService.getAll();
	}
}
