package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import bilalbyg.spotifyClone.business.abstracts.SongService;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/songs")
@AllArgsConstructor
public class SongsController {
	
	private SongService songService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public void add(@RequestBody Song song) {
		this.songService.add(song);
	}
	
	@GetMapping("/getall")
	public List<Song> getAll(){
		return this.songService.getAll();
	}
}
