package com.epam.edai.run8.team11.repository.waiter;

import com.epam.edai.run8.team11.model.user.Waiter;

import java.util.List;

public interface WaiterRepository {
    List<Waiter> getAllWaiterAtLocation(String locationId);
    void update(Waiter waiter);
    Waiter findByID(String id);
}
