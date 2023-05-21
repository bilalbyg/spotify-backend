package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import bilalbyg.spotifyClone.business.abstracts.ArtistService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Album;
import bilalbyg.spotifyClone.entities.concretes.Artist;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/artists")
@CrossOrigin
public class ArtistsController {
	
	private ArtistService artistService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Artist artist) {
		return this.artistService.add(artist);
	}
	
	@GetMapping("/getall")
	public DataResult<List<Artist>> getAll(){
		return this.artistService.getAll();
	}
	
	@GetMapping("/getByName")
	public DataResult<Artist> getByName(@RequestParam String name){
		return this.artistService.getByName(name);
	}
	
	@GetMapping("/getById")
	public DataResult<Artist> getById(@RequestParam int artistId){
		return artistService.getById(artistId);
	}
	
	@GetMapping("/getByNameContains")
	public DataResult<List<Artist>> getByNameContains(@RequestParam String name){
		return this.artistService.getByNameContains(name);
	}
	
	@GetMapping("/getByNameStartsWith")
	public DataResult<List<Artist>> getByNameStartsWith(String name){
		return this.artistService.getByNameStartsWith(name);
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Artist artist) {
		return this.artistService.update(artist);
	}
	
	@GetMapping("/getArtistsById")
	public DataResult<List<Artist>> getArtistsById(@RequestParam List<Integer> ids){
		return artistService.getArtistsById(ids);
	}
}
