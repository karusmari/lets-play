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

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public User createUser(User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (!user.getName().matches("[a-zA-ZäöüÄÖÜ\\-' ]+")) {
            throw new IllegalArgumentException("Name contains invalid characters");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!user.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Email is invalid");
        }

        // checking the password
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            throw new IllegalArgumentException("Password must be at least 3 characters");
        }

        // set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // default
        }

        // hashing password before saving
        String rawPassword = user.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encodedPassword);


        try {
            return userRepository.save(user);
        } catch (Exception e) {
            // in case there is a duplication error in the database
            if (e.getMessage().contains("duplicate key error")) {
                throw new IllegalArgumentException("User with this email already exists");
            }
            throw e;
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.username")
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public User findByIdOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.username")
    public User updateUser(String userId, User user) {
        User existingUser = userRepository.findById(userId).orElseThrow();
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        return userRepository.save(existingUser);
    }

    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal.username")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}

//service is responsible for business logic and data manipulation. It chooses how to handle data and interacts with the repository layer.
//it doesn't handle HTTP requests directly, that's the controller's job.
//Service can validate, filter, do calculations, and enforce business rules before passing data to/from the repository.
