package com.epam.edai.run8.team11.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaiterProfileDto {
    private String firstName;
    private String lastName;
    private String locationId;
    private String imageUrl;
}
