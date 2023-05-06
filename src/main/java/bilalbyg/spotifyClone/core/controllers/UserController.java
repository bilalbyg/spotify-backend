package bilalbyg.spotifyClone.core.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bilalbyg.spotifyClone.core.business.abstracsts.UserService;
import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin
public class UserController {
	
	private UserService userService;
	
	@GetMapping("/getall")
	public DataResult<List<User>> getAll(){
		return userService.getAll();
	}
	
	@GetMapping("/getById/{id}")
	public DataResult<User> getById(@PathVariable int id) {
		return userService.getById(id);
	}
}
