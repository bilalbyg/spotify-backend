package bilalbyg.spotifyClone.webApi.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bilalbyg.spotifyClone.business.abstracts.CategoryService;
import bilalbyg.spotifyClone.core.utilities.DataResult;
import bilalbyg.spotifyClone.core.utilities.Result;
import bilalbyg.spotifyClone.entities.concretes.Category;
import bilalbyg.spotifyClone.entities.concretes.Song;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
@CrossOrigin
public class CategoriesController {
	private CategoryService categoryService;
	
	@PostMapping("/add")
	@ResponseStatus(code=HttpStatus.CREATED)
	public Result add(@RequestBody Category category) {
		return this.categoryService.add(category); // *** delete this's later *** 
	}
	
	@GetMapping("/getall")
	public DataResult<List<Category>> getAll() {
		return this.categoryService.getAll(); 
	}
	
	@GetMapping("/getByCategoryId")
	public DataResult<Category> getById(@RequestParam int categoryId){
		return categoryService.getByCategoryId(categoryId);
	}
	
	@PutMapping("/update")
	public Result update(@RequestBody Category category) {
		return this.categoryService.update(category);
	}
	
	@DeleteMapping("/delete")
	public Result update(@RequestParam int id) {
		return this.categoryService.delete(id);
	}
}
