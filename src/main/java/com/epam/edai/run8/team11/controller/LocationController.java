package com.epam.edai.run8.team11.controller;


import com.epam.edai.run8.team11.dto.feedback.LocationFeedbackDTO;
import com.epam.edai.run8.team11.dto.location.LocationSelectOptionDTO;
import com.epam.edai.run8.team11.model.Location;
import com.epam.edai.run8.team11.model.dish.Dish;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.service.location.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/locations")
@Slf4j
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location savedLocation = locationService.saveLocation(location);
        return ResponseEntity.ok(savedLocation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> findLocationById(@PathVariable String id) {
        Location location = locationService.findLocationById(id);
        return ResponseEntity.ok(location);
    }

    @GetMapping()
    public ResponseEntity<List<Location>> findAllLocations() {
        List<Location> locations = locationService.findAll();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/select-options")
    public ResponseEntity<List<LocationSelectOptionDTO>> findAllSelectOptionDtos() {
        List<LocationSelectOptionDTO> selectOptionDtos = locationService.findAllSelectOptionDtos();
        return ResponseEntity.ok(selectOptionDtos);
    }

    @GetMapping("/{id}/feedbacks")
    public ResponseEntity<Page<LocationFeedbackDTO>> findAllFeedbacksByLocationId(
            @PathVariable String id,
            @RequestParam(defaultValue = "CUISINE_EXPERIENCE") FeedbackType type,
            @RequestParam(defaultValue = "date,asc") String sort,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "0") Integer page
    ){
        log.info("/{}/feedbacks?type={}&sort={}&size={}&page={}", id, type, sort, size, page);
        Page<LocationFeedbackDTO> feedbackDTOS = locationService.findLocationFeedbacksByLocationIdWithPagination(id, type, sort, size, page);
        return ResponseEntity.ok(feedbackDTOS);
    }

    @GetMapping("/{id}/speciality-dishes")
    public ResponseEntity<List<Dish>> findAllSpecialityDishesByLocationId(@PathVariable String id){
        List<Dish> specialityDishes = locationService.findSpecialityDishesByLocationId(id);
        return ResponseEntity.ok(specialityDishes);
    }
}