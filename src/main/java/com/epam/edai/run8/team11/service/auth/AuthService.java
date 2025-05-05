package com.epam.edai.run8.team11.service.auth;


import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.SignInDto;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.service.user.UserService;
import com.epam.edai.run8.team11.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public ServiceBodyDto<String> loginUser(SignInDto signInDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword()));

        if(authentication.isAuthenticated()) {
            ServiceBodyDto<Optional<UserDto>> userByPartitionKey = userService.getUserByPartitionKey(signInDto.getEmail());
            if(userByPartitionKey.getData().isPresent()){
                String s = jwtUtil.generateToken(userByPartitionKey.getData().get());
                log.info("Token -> {}", s);
                return ServiceBodyDto.success(s, "Token Generated");
            }
        }

        return ServiceBodyDto.error("Error Generating JWT_Token", HttpStatus.EXPECTATION_FAILED);
    }
}
