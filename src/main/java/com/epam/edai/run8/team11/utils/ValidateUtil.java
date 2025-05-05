package com.epam.edai.run8.team11.utils;

import com.epam.edai.run8.team11.dto.SignInDto;
import com.epam.edai.run8.team11.dto.SignUpDto;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ValidateUtil {

    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
    public static final String FIRST_NAME = "^[a-zA-Z]{3,50}(?: [a-zA-Z]+)?$";
    public static final String LAST_NAME = "^[a-zA-Z]{3,50}$";

    public List<String> validateSignUpRequestBody(SignUpDto user){
        List<String> invalidInputs = new ArrayList<>();
        if(user == null){
            invalidInputs.addAll(List.of("firstName", "lastName", "email", "password"));
            return invalidInputs;
        }

        if(user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().matches(EMAIL)){
            invalidInputs.add("email");
        }

        if(user.getFirstName() == null || user.getFirstName().isEmpty() || !user.getFirstName().matches(FIRST_NAME)){
            invalidInputs.add("firstName");
        }

        if(user.getLastName() == null || user.getLastName().isEmpty() || !user.getLastName().matches(LAST_NAME)){
            invalidInputs.add("lastName");
        }

        if(user.getPassword() == null || user.getPassword().isEmpty() || !user.getPassword().matches(PASSWORD)){
            invalidInputs.add("password");
        }

        return invalidInputs;
    }


    public List<String> validateSignInRequestBody(SignInDto user){
        List<String> invalidInputs = new ArrayList<>();
        if(user == null){
            invalidInputs.addAll(List.of("email", "password"));
            return invalidInputs;
        }

        if(user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().matches(EMAIL)){
            invalidInputs.add("email");
        }

        if(user.getPassword() == null || user.getPassword().isEmpty() || !user.getPassword().matches(PASSWORD)){
            invalidInputs.add("password");
        }

        return invalidInputs;
    }
}
