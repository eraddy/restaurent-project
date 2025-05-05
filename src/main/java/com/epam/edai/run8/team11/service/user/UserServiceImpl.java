package com.epam.edai.run8.team11.service.user;

import com.epam.edai.run8.team11.dto.RepositoryBodyDto;
import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.dto.user.UserDtoMapper;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public ServiceBodyDto<Optional<UserDto>> getUserByPartitionKey(String email) {
        RepositoryBodyDto<Optional<User>> result = userRepository.getByPartitionKey(email);
        if(result.isSuccess()){
            Optional<UserDto> userDto = result
                    .getData()
                    .map(userDtoMapper);


            return ServiceBodyDto.success(
                    userDto
            );
        }

        return ServiceBodyDto.error(result.getMessage(), result.getErrorCode());
    }

    @Override
    public ServiceBodyDto<Void> saveUser(User user) {
        RepositoryBodyDto<Void> save = userRepository.save(user);
        if(save.isSuccess()){
            return ServiceBodyDto.success(save.getMessage());
        }

        if(save.getErrorCode() == HttpStatus.CONFLICT){
            return ServiceBodyDto.error(save.getMessage(), save.getErrorCode());
        }

        return ServiceBodyDto.error(save.getMessage());
    }

    @Override
    public ServiceBodyDto<Optional<UserDto>> getUserByUserId(String userId) {
        RepositoryBodyDto<Optional<User>> result = userRepository.getByUserId(userId);

        if(result.isSuccess()){
            Optional<UserDto> userDto = result.getData()
                    .map(userDtoMapper);

            return ServiceBodyDto.success(userDto, result.getMessage());
        }

        return ServiceBodyDto.error(result.getMessage(), result.getErrorCode());
    }
}
