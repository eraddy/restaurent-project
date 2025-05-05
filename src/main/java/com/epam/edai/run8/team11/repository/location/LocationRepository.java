package com.epam.edai.run8.team11.repository.location;

import com.epam.edai.run8.team11.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    String TABLE_NAME = "locations";

    void save(Location location);
    Optional<Location> findById(String locationId);
    List<Location> findAll();
    void deleteById(String locationId);
}
