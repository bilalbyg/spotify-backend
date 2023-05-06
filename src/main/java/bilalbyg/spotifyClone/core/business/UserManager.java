package bilalbyg.spotifyClone.core.business;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.core.business.abstracsts.UserService;
import bilalbyg.spotifyClone.core.dataAccess.UserRepository;
import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.entities.concretes.Category;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService{
	
	private UserRepository userRepository;

	@Override
	public DataResult<List<User>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<User>>(userRepository.findAll(), "All users listed");
	}

	@Override
	public DataResult<User> getById(int id) {
		// TODO Auto-generated method stub
		return new SuccessDataResult<User>(userRepository.findById(id).orElse(null), "User listed");
	}

	@Override
	public Result add(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result update(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
