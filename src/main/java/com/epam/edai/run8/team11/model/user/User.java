package com.epam.edai.run8.team11.model.user;


import com.epam.edai.run8.team11.model.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;


@Data
@AllArgsConstructor
@DynamoDbBean
@Builder
@NoArgsConstructor
public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String password;
    private String userAvtarUrl;

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }
}