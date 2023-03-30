package bilalbyg.spotifyClone.business.abstracts;

import java.util.List;

import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Category;

public interface CategoryService {
	Result add(Category category);
	Result update(Category category);
	Result delete(int id);
	DataResult<List<Category>> getAll();
}
