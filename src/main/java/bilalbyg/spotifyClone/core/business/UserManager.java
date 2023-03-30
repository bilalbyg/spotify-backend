package bilalbyg.spotifyClone.core.business;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.core.dataAccess.UserRepository;
import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.entities.concretes.Category;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager {
	
	private UserRepository userRepository;
	
	public DataResult<List<User>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<User>>(userRepository.findAll(), "All users listed");
	}
}
