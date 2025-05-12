package com.epam.edai.run8.team11.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}
