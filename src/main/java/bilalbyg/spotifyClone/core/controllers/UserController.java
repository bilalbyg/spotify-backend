package bilalbyg.spotifyClone.core.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.DataResult;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	
	@GetMapping("/getall")
	public DataResult<List<User>> getAll(){
		return null;
	}
	
}
