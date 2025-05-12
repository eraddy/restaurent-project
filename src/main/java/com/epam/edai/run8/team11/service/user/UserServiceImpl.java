package com.epam.edai.run8.team11.service.user;

import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.user.profile.UserProfileDetailsDto;
import com.epam.edai.run8.team11.dto.user.profile.UserProfileDetailsDtoMapper;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.dto.user.UserDtoMapper;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.repository.user.UserRepository;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final UserProfileDetailsDtoMapper userProfileDetailsDtoMapper;
    private final AuthenticationUtil authenticationUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ServiceBodyDto<Optional<UserDto>> getUserByPartitionKey(String email) {
        Optional<User> result = userRepository.findById(email);
        Optional<UserDto> userDto = Optional.empty();

        if(result.isPresent()){
            userDto = result
                    .map(userDtoMapper);

            return ServiceBodyDto.success(
                    userDto
            );
        }

        return ServiceBodyDto.error("User Not Found", HttpStatus.NOT_FOUND);
    }

    @Override
    public ServiceBodyDto<Void> saveUser(User user) {
        userRepository.save(user);
        return ServiceBodyDto.success("User Created");
    }

    @Override
    public List<UserProfileDetailsDto> findAllUserDetails() {
        return userRepository.findAll().stream()
                .map(userProfileDetailsDtoMapper)
                .toList();
    }

    @Override
    public ServiceBodyDto<Optional<UserDto>> getUserByUserId(String userId) {
        Optional<User> result = userRepository.findByUserId(userId);
        Optional<UserDto> userDto = Optional.empty();

        if(result.isPresent()) {
            userDto = result
                    .map(userDtoMapper);

        }

        return userDto.isPresent() ? ServiceBodyDto.success(userDto, "User Found") :
                ServiceBodyDto.error("User Not Found", HttpStatus.NOT_FOUND);

    }

    @Override
    public ServiceBodyDto<UserDto> getUserProfileDetails() {
        Optional<UserDto> authenticatedUser = authenticationUtil.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            UserDto userDto = authenticatedUser.get();

            return ServiceBodyDto.success(userDto, "User Found");
        }

        return ServiceBodyDto.error("User Not Signed In", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ServiceBodyDto<Void> updateUser(String firstName, String lastName) {
        Optional<UserDto> authenticatedUser = authenticationUtil.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            UserDto userDto = authenticatedUser.get();
            Optional<User> byId = userRepository.findById(userDto.getEmail());

            if (byId.isEmpty()) {
                return ServiceBodyDto.error("User Not Found", HttpStatus.NOT_FOUND);
            }

            User user = byId.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userRepository.updateUser(user);
            return ServiceBodyDto.success("Profile has been successfully updated");
        }

        return ServiceBodyDto.error("User Not Signed In", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ServiceBodyDto<Void> updateUserPassword(String oldPassword, String newPassword) {
        Optional<UserDto> authenticatedUser = authenticationUtil.getAuthenticatedUser();
        if (authenticatedUser.isPresent()) {
            UserDto userDto = authenticatedUser.get();
            Optional<User> byId = userRepository.findById(userDto.getEmail());

            if (byId.isEmpty()) {
                return ServiceBodyDto.error("User Not Found", HttpStatus.NOT_FOUND);
            }

            User user = byId.get();

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ServiceBodyDto.error("Old Password is Incorrect", HttpStatus.BAD_REQUEST);
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.updateUser(user);
            return ServiceBodyDto.success("Password has been successfully updated");
        }

        return ServiceBodyDto.error("User Not Signed In", HttpStatus.UNAUTHORIZED);
    }


}
