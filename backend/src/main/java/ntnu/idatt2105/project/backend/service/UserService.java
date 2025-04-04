package ntnu.idatt2105.project.backend.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ntnu.idatt2105.project.backend.model.User;
import ntnu.idatt2105.project.backend.repository.UserRepo;


/*
 * This class implements the UserDetailsService interface to load user-specific data.
 * It is used by Spring Security to retrieve user details during authentication.
 * The loadUserByUsername method retrieves the user from the database using the UserRepo interface.
 * If the user is not found, it throws a UsernameNotFoundException.
 * The method returns a UserDetails object containing the user's information.
 */
@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;

    /*
     * This method is called by Spring Security to load user-specific data.
     * It retrieves the user from the database using the UserRepo interface.
     * If the user is not found, it throws a UsernameNotFoundException.
     * The method returns a UserDetails object containing the user's information.
     * 
     * @param email The email of the user to be loaded.
     * @return A UserDetails object containing the user's information.
     * @throws UsernameNotFoundException If the user is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
    }
}
