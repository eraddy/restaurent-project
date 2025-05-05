package com.epam.edai.run8.team11.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;
import java.util.Map;


@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Waiter extends User{

    private Map<String, List<String>> slots;
    private String locationId;
    private Integer count;
}
