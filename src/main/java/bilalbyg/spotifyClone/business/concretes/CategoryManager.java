package bilalbyg.spotifyClone.business.concretes;

import java.util.List;

import org.springframework.stereotype.Service;

import bilalbyg.spotifyClone.business.abstracts.CategoryService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.core.utilities.SuccessDataResult;
import bilalbyg.spotifyClone.core.utilities.SuccessResult;
import bilalbyg.spotifyClone.dataAccess.abstracts.CategoryRepository;
import bilalbyg.spotifyClone.entities.concretes.Category;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class CategoryManager implements CategoryService{

	private CategoryRepository categoryRepository;
	
	@Override
	public Result add(Category category) {
		// TODO Auto-generated method stub
		this.categoryRepository.save(category);
		return new SuccessResult("Category added");
	}

	@Override
	public DataResult<List<Category>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Category>>(this.categoryRepository.findAll(), "All categories listed");
	}

	@Override
	public Result update(Category category) {
		// TODO Auto-generated method stub
		this.categoryRepository.save(category);
		return new SuccessResult("Category updated");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		this.categoryRepository.deleteById(id);
		return new SuccessResult("Category deleted");
	}

}
