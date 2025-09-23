package com.letsplay.controller;

import com.letsplay.model.User;
import com.letsplay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.letsplay.dto.UserResponse;



@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public Optional<User> findById(@PathVariable String userId) {
        return userService.findById(userId);
    }

    @PutMapping("/{userId}")
    public User updateUser(@PathVariable String userId, @RequestBody User user) {
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        String currentUserId = userService.getCurrentUserId();
        User user = userService.findById(currentUserId).orElseThrow();
        return new UserResponse(user.getName(), user.getEmail());
    }

}


