package ntnu.idatt2105.project.backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ntnu.idatt2105.project.backend.model.User;


/*
 * This interface extends the CrudRepository interface to provide CRUD operations for the User entity.
 * It defines methods to find users by username and email, and to delete a user by ID.
 * The interface is annotated with @Repository to indicate that it is a Spring Data repository.
 */
@Repository
public interface UserRepo extends CrudRepository<User, Long> {
   Optional<User> findByEmail(String email);
}