package bilalbyg.spotifyClone.core.business.abstracsts;

import java.util.List;

import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;

public interface UserService {
	
	DataResult<List<User>> getAll();
	DataResult<User> getById(int id);
	Result add(User user);
	Result update(User user);
	Result delete(int id);
}
