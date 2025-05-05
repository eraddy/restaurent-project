package com.epam.edai.run8.team11.repository.waiter;

import com.epam.edai.run8.team11.model.user.Waiter;

import java.util.List;
import java.util.Optional;

public interface WaiterRepository {
    List<Waiter> getAllWaiterAtLocation(String locationId);
    void update(Waiter waiter);
    Optional<Waiter> findById(String id);
    Optional<Waiter> findByEmail(String email);
}
