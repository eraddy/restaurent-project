package com.epam.edai.run8.team11.service.location;

import com.epam.edai.run8.team11.dto.feedback.LocationFeedbackDTO;
import com.epam.edai.run8.team11.dto.feedback.LocationFeedbackDTOMapper;
import com.epam.edai.run8.team11.dto.location.LocationSelectOptionDTO;
import com.epam.edai.run8.team11.dto.location.LocationSelectOptionDTOMapper;
import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.model.Location;
import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import com.epam.edai.run8.team11.repository.location.LocationRepository;
import com.epam.edai.run8.team11.service.dish.DishService;
import com.epam.edai.run8.team11.service.feedback.FeedbackService;
import com.epam.edai.run8.team11.service.presignedimage.PresignedImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationSelectOptionDTOMapper locationSelectOptionDTOMapper;
    private final LocationFeedbackDTOMapper locationFeedbackDTOMapper;
    private final DishService dishService;
    private final PresignedImageService imageService;

    @Autowired
    @Lazy
    private FeedbackService feedbackService;

    public Location saveLocation(Location location) {
        locationRepository.save(location);
        return location;
    }

    public Location findLocationById(String locationId) {
        if(locationId == null || locationId.isEmpty())
            throw new InvalidInputException(Location.LOCATION_ID, locationId);
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));
        location.setImageUrl(imageService.generatePresignedUrlForImage(location.getImageUrl()));
        return location;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll()
                .stream()
                .peek(location -> location.setImageUrl(
                        imageService.generatePresignedUrlForImage(location.getImageUrl()))
                ).toList();
    }

    @Override
    public List<LocationSelectOptionDTO> findAllSelectOptionDtos() {
        return findAll().stream().map(locationSelectOptionDTOMapper).toList();
    }

    @Override
    public Page<LocationFeedbackDTO> findLocationFeedbacksByLocationIdWithPagination(String locationId, FeedbackType type, String sort, Integer size, Integer page) {
        if(locationId == null || locationId.isEmpty())
            throw new InvalidInputException(Location.LOCATION_ID, locationId);
        locationRepository.findById(locationId).orElseThrow(() -> new LocationNotFoundException(locationId));
        Page<Feedback> feedbackPage = feedbackService.findByLocationIdWithPagination(locationId, type, sort, size, page);
        return feedbackPage.map(locationFeedbackDTOMapper);
    }

    @Override
    public List<Dish> findSpecialityDishesByLocationId(String locationId) {
        if(locationId == null || locationId.isEmpty())
            throw new InvalidInputException(Location.LOCATION_ID, locationId);
        locationRepository.findById(locationId).orElseThrow(() -> new LocationNotFoundException(locationId));
        return dishService.findSpecialityDishesByLocationId(locationId);
    }

}