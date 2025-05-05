package com.epam.edai.run8.team11.repository.user;

import com.epam.edai.run8.team11.dto.RepositoryBodyDto;
import com.epam.edai.run8.team11.model.user.User;

import java.util.Optional;

public interface UserRepository {
    RepositoryBodyDto<Void> save(User user);
    RepositoryBodyDto<Optional<User>> getByPartitionKey(String email);
    RepositoryBodyDto<Optional<User>> getByUserId(String userId);
}
