package com.epam.edai.run8.team11.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class DefaultDto {
    private String email;
    private String locationId;
    private String role;

    @DynamoDbPartitionKey
    public String getEmail() {
        return email;
    }
}
