package com.letsplay.service;

import com.letsplay.model.User;
import com.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import com.letsplay.security.SecurityUtils;
import static com.letsplay.security.SecurityUtils.isAdmin;
import com.letsplay.dto.UpdateUserRequest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(User user) {
        checkEmailUniqueness(user.getEmail());
        prepareUserForSave(user);

        try {
            return userRepository.save(user);
        } catch (Exception e) {
            handleSaveException(e);
            return null;
            }
        }


        // Check if email is unique
        private void checkEmailUniqueness(String email) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }

        // Prepare user object before saving - set default role and encode password
        private void prepareUserForSave(User user) {
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Handle exceptions during save operation to the database
        private void handleSaveException(Exception e) {
            if (e.getMessage().contains("duplicate key error")) { // MongoDB specific error message
                throw new IllegalArgumentException("User with this email already exists");
            }
            throw new RuntimeException(e); // if some other error occurs
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // method to get current logged-in user id
    public String getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    // method to find user by id, only admin can access this method
    @PreAuthorize("hasAuthority('ADMIN')")
    public Optional<User> findById(String userId) { // optional means it may or may not contain a non-null value
        return userRepository.findById(userId);
    }

    // method to find user by id or throw exception if not found
    public User findByIdOrThrow(String userId) { // used for /me and for the sellerName
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // method to find user by email, used in authentication
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // method to update user, only admin can access this method
    @PreAuthorize("hasAuthority('ADMIN')")
    public User updateUser(String userId, UpdateUserRequest request) {
        if (!isAdmin()) {
            throw new RuntimeException("Forbidden - only admins can modify users");
        }

        // Fetch existing user from DB
        User existingUser = userRepository.findById(userId).orElseThrow();

        if (request.getName() != null) existingUser.setName(request.getName());
        if (request.getEmail() != null) existingUser.setEmail(request.getEmail());

        // Only update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}

//service is responsible for business logic and data manipulation. It chooses how to handle data and interacts with the repository layer.
//it doesn't handle HTTP requests directly, that's the controller's job.
//Service can validate, filter, do calculations, and enforce business rules before passing data to/from the repository.
