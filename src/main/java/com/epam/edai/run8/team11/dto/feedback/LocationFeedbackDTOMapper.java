package com.epam.edai.run8.team11.dto.feedback;

import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.exception.user.UserNotFoundException;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class LocationFeedbackDTOMapper implements Function<Feedback, LocationFeedbackDTO> {

    private final UserService userService;

    @Override
    public LocationFeedbackDTO apply(Feedback feedback) {
        UserDto userDto = userService.getUserByPartitionKey(feedback.getUserEmail()).getData()
                .orElseThrow(() -> new UserNotFoundException(feedback.getUserEmail()));
        return LocationFeedbackDTO.builder()
                .id(feedback.getFeedbackId())
                .locationId(feedback.getLocationId())
                .type(feedback.getType())
                .date(feedback.getDate())
                .rating(feedback.getRating())
                .comment(feedback.getReview())
                .author(userDto.getFullName())
                .build();
    }
}
