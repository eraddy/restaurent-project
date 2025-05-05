package com.epam.edai.run8.team11.model.user;


import com.epam.edai.run8.team11.model.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;


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

    private static final String USER_ID_INDEX_NAME = "userId-index";

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = USER_ID_INDEX_NAME)
    public String getUserId() {
        return userId;
    }
}