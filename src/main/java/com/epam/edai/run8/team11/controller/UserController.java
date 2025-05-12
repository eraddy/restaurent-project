package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.user.UpdatePasswordDto;
import com.epam.edai.run8.team11.dto.user.UpdateDto;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.service.user.UserService;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import com.epam.edai.run8.team11.utils.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ValidateUtil validateUtil;
    private final ResponseUtil responseUtil;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(){
        return ResponseEntity.ok(Map.of("üsers", userService.findAllUserDetails())) ;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(){
        ServiceBodyDto<UserDto> userProfileDetails = userService.getUserProfileDetails();
        if(userProfileDetails.isSuccess()){
            return responseUtil.buildOkResponse(Map.of("user", userProfileDetails.getData()));
        } else {
            return responseUtil.buildUnauthorized(userProfileDetails.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody UpdateDto updateDto){
        List<String> strings = validateUtil.validateUserUpdateRequest(updateDto);

        if(!strings.isEmpty()){
            return responseUtil.buildBadRequestResponse(Map.of("error", true, "invalidInputs", strings));
        }

        ServiceBodyDto<Void> userProfileDetails = userService.updateUser(updateDto.getFirstName(), updateDto.getLastName());
        if(userProfileDetails.isSuccess()){
            return responseUtil.buildOkResponse(Map.of("message", userProfileDetails.getMessage()));
        } else {
            return responseUtil.buildUnauthorized(userProfileDetails.getMessage());
        }
    }

    @PutMapping("/profile/password")
    public ResponseEntity<Map<String, Object>> updateUserPassword(@RequestBody UpdatePasswordDto updateDto){
        List<String> strings = validateUtil.validateUserPasswordUpdateRequest(updateDto);

        if(!strings.isEmpty()){
            return responseUtil.buildBadRequestResponse(Map.of("error", true, "invalidInputs", strings));
        }

        ServiceBodyDto<Void> userProfileDetails = userService.updateUserPassword(updateDto.getOldPassword(), updateDto.getNewPassword());
        if(userProfileDetails.isSuccess()){
            return responseUtil.buildOkResponse(Map.of("message", userProfileDetails.getMessage()));
        } else {
            return responseUtil.buildUnauthorized(userProfileDetails.getMessage());
        }
    }
}
