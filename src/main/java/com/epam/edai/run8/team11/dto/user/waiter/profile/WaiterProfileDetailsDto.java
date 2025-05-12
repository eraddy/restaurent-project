package com.epam.edai.run8.team11.dto.user.waiter.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WaiterProfileDetailsDto {
    private String firstName;
    private String lastName;
    private String locationId;
    private String imageUrl;
}
