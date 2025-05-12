package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.SignInDto;
import com.epam.edai.run8.team11.dto.SignUpDto;
import com.epam.edai.run8.team11.dto.user.SignUpDtoUserMapper;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.service.auth.AuthService;
import com.epam.edai.run8.team11.service.user.UserServiceImpl;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import com.epam.edai.run8.team11.utils.ValidateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final ValidateUtil validateUtil;
    private final ResponseUtil responseUtil;
    private final UserServiceImpl userService;
    private final SignUpDtoUserMapper signUpDtoUserMapper;
    private final AuthService authService;
    private final AuthenticationUtil authenticationUtil;

    private static final String MESSAGE = "message";

    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody SignUpDto signUpDto){
        List<String> invalidInputs = validateUtil.validateSignUpRequestBody(signUpDto);
        if(!invalidInputs.isEmpty()){
            return responseUtil.buildBadRequestResponse(Map.of("error",true,
                    MESSAGE, "Invalid input: " + invalidInputs));
        }

        User user = signUpDtoUserMapper.apply(signUpDto);

        ServiceBodyDto<Void> saveUser = userService.saveUser(user);

        if(saveUser.isSuccess()){
            return responseUtil.buildCreateResponse(Map.of(MESSAGE, saveUser.getMessage()));
        }

        if(saveUser.getErrorCode() == HttpStatus.CONFLICT){
            return responseUtil.buildConflictResponse(Map.of("error",true
                    ,MESSAGE, saveUser.getMessage()));
        }

        return responseUtil.buildInternalServerResponse(Map.of("error",true,
                MESSAGE, saveUser.getMessage()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody SignInDto signInDto){
        List<String> invalidInputs = validateUtil.validateSignInRequestBody(signInDto);
        if(!invalidInputs.isEmpty()){
            return responseUtil.buildBadRequestResponse(Map.of("error",true,
                    MESSAGE, "Invalid input: " + invalidInputs));
        }

        ServiceBodyDto<String> userLogin = authService.loginUser(signInDto);
        log.info("User > {}", userLogin);
        log.info("Token -> {}", userLogin.getData());

        if(userLogin.isSuccess()){
            ServiceBodyDto<Optional<UserDto>> user = userService.getUserByPartitionKey(signInDto.getEmail());

            // Check if user data exists and is valid
            if(user.getData().isPresent()) {
                UserDto userData = user.getData().get();
                String username = (userData.getFirstName() != null ? userData.getFirstName() : "") + " " +
                        (userData.getLastName() != null ? userData.getLastName() : "");

                // Create a mutable map to avoid null value issues
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("accessToken", userLogin.getData());
                responseData.put("username", username);

                if(userData.getRole() != null) {
                    responseData.put("role", userData.getRole());
                }

                return responseUtil.buildOkResponse(responseData);
            } else {
                // Handle a case where user data is not present
                return responseUtil.buildInternalServerResponse(
                        Map.of(
                                "error",true,
                                MESSAGE, "User data not found after successful authentication"
                        )
                );
            }
        }

        return responseUtil.buildInternalServerResponse(Map.of(
                "error",true,MESSAGE, "Unable to Login: " + userLogin.getMessage()));
    }

    @GetMapping("/user-profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Optional<UserDto> userOptional = authenticationUtil.getAuthenticatedUser();

        if (userOptional.isPresent()) {
            UserDto userDto = userOptional.get();

            // Create response with user details
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("userId", userDto.getUserId());
            userProfile.put("email", userDto.getEmail());
            userProfile.put("firstName", userDto.getFirstName());
            userProfile.put("lastName", userDto.getLastName());
            userProfile.put("role", userDto.getRole());

            return responseUtil.buildOkResponse(userProfile);
        } else {
            return responseUtil.buildBadRequestResponse(
                    Map.of(MESSAGE, "User is not authenticated")
            );
        }
    }
}
