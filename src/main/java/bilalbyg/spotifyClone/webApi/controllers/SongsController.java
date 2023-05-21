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
import bilalbyg.spotifyClone.business.abstracts.SongService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/songs")
@AllArgsConstructor
@CrossOrigin
public class SongsController {
	
	private SongService songService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Song song) {
		return this.songService.add(song);
	}
	
	@GetMapping("/getall")
	public DataResult<List<Song>> getAll(){
		return this.songService.getAll();
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Song song) {
		return this.songService.update(song);
	}
	
	@GetMapping("/getById")
	public DataResult<Song> getById(@RequestParam int id){
		return songService.getById(id);
	}
	
	@GetMapping("/getSongsById")
	public DataResult<List<Song>> getSongsById(@RequestParam List<Integer> ids){
		return songService.getSongsById(ids);
	}
	
	@GetMapping("/getSongsByAlbumId")
	public DataResult<List<Song>> getSongsByAlbumId(@RequestParam int albumId){
		return songService.getSongsByAlbumId(albumId);
	}
	
	@GetMapping("/getSongsByCategoryId")
	public DataResult<List<Song>> getSongsByCategoryId(@RequestParam int categoryId){
		return songService.getSongsByCategoryId(categoryId);
	}
}
