package com.epam.edai.run8.team11.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Data
@Builder
public class Table {
    private String locationId; // Partition Key
    private Integer tableNumber; // Sort Key
    private String locationAddress;
    private int capacity; // Max guests allowed
    @JsonProperty("availableSlots")
    private Map<String, List<String>> slots;

    @DynamoDbPartitionKey
    public String getLocationId(){return locationId;}

    @DynamoDbSortKey
    public Integer getTableNumber(){return tableNumber;}
}
