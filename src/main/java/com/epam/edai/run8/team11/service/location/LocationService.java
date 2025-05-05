package com.epam.edai.run8.team11.service.location;

import com.epam.edai.run8.team11.dto.feedback.LocationFeedbackDTO;
import com.epam.edai.run8.team11.dto.location.LocationSelectOptionDTO;
import com.epam.edai.run8.team11.model.Location;
import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LocationService {
    public Location saveLocation(Location location);
    public Location findLocationById(String locationId);
    public List<Location> findAll();
    public List<LocationSelectOptionDTO> findAllSelectOptionDtos();
    public Page<LocationFeedbackDTO> findLocationFeedbacksByLocationIdWithPagination(String locationId, FeedbackType type, String sort, Integer size, Integer page);
    public List<Dish> findSpecialityDishesByLocationId(String locationId);
}