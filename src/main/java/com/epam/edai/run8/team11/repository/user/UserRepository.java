package com.epam.edai.run8.team11.repository.user;

import com.epam.edai.run8.team11.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    void save(User user);
    Optional<User> findById(String email);
    Optional<User> findByUserId(String userId);
    User updateUser(User user);
}
