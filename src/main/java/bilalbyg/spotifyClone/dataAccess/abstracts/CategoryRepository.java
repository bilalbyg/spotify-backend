package bilalbyg.spotifyClone.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.entities.concretes.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
