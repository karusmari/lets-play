package com.letsplay.repository;

import com.letsplay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

// interface chooses only the methods but doesn't implement them
// implementation is done by Spring Data MongoDB at runtime
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findById(String userId); //MongoDb's id
    Optional<User> findByEmail(String email);

}

// Service is calling out to the repository to fetch or save data
// Repository is only responsible for data access and storage

