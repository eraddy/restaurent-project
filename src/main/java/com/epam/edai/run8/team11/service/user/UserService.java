package com.epam.edai.run8.team11.service.user;

import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.user.UserDetailsDto;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ServiceBodyDto<Void> saveUser(User user);
    List<UserDetailsDto> findAllUserDetails();
    ServiceBodyDto<Optional<UserDto>> getUserByPartitionKey(String email);
    ServiceBodyDto<Optional<UserDto>> getUserByUserId(String userId);
}
