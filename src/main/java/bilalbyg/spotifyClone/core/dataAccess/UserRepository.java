package bilalbyg.spotifyClone.core.dataAccess;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bilalbyg.spotifyClone.core.entities.User;
import bilalbyg.spotifyClone.entities.concretes.Album;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByUserMail(String userMail);
}
