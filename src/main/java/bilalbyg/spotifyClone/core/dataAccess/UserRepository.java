package bilalbyg.spotifyClone.core.dataAccess;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.core.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByUserMail(String userMail);
}
