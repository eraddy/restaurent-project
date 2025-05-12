package com.epam.edai.run8.team11.model.table.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class TableResponseDto {
    private String locationId; // Partition Key
    private Integer tableNumber; // Sort Key
    private int capacity; // Max guests allowed
    private String locationAddress;
    @JsonProperty("availableSlots")
    private Map<String, List<String>> slots;
}
