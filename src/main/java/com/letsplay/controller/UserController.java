package com.letsplay.controller;

import com.letsplay.model.User;
import com.letsplay.security.JwtUtil;
import com.letsplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;




@RestController // indicates that this class is a REST controller and handles HTTP requests
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    //creating a new user
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    //user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole()));
    }

    //getting all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //getting a specific user by ID
    @GetMapping("/{userId}")
    public Optional<User> findById(@PathVariable String userId) {
        return userService.findById(userId);
    }

    //updating a specific user by ID
    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    //deleting a specific user by ID
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

}

// This controller handles HTTP requests related to user management, such as creating, retrieving, updating, and deleting users.
// It delegates the actual business logic to the UserService, ensuring a clean separation of concerns.
// controller listens to the URL paths (/users/...) and receives get/post/put/delete requests,
// then calls the appropriate service methods to perform the requested operations.
// it does not contain business logic, but uses/calls out the service for that purpose.

