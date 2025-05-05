package com.epam.edai.run8.team11.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class DefaultDto {
    private String email;
    private String locationId;
    private String role;
}
