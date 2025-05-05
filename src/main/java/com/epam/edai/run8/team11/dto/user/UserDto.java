package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.model.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String userId;

    @Override
    public String toString() {
        return "{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getFullName(){
        return String.format("%s %s", firstName, lastName);
    }
}
