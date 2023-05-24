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

import bilalbyg.spotifyClone.business.abstracts.AlbumService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Album;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/albums")
@AllArgsConstructor
@CrossOrigin
public class AlbumsController {
	
	private AlbumService albumService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Album album) {
		return this.albumService.add(album);
	}
	
	@GetMapping("/getall")
	public DataResult<List<Album>> getAll(){
		return this.albumService.getAll();
	}
	
	@GetMapping("/getByAlbumId")
	public DataResult<Album> getByAlbumId(@RequestParam int albumId){
		return albumService.getByAlbumId(albumId);
	}
	
	@GetMapping("/getByArtistId")
	public DataResult<List<Album>> getByArtistId(@RequestParam int artistId){
		return albumService.getByArtistId(artistId);
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Album album) {
		return this.albumService.update(album);
	}
	
	@GetMapping("/getAlbumsById")
	public DataResult<List<Album>> getAlbumsById(@RequestParam List<Integer> ids){
		return albumService.getAlbumsById(ids);
	}
}
