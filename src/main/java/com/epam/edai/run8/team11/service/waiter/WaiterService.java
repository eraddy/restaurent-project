package com.epam.edai.run8.team11.service.waiter;

import com.epam.edai.run8.team11.dto.user.WaiterProfileDto;
import com.epam.edai.run8.team11.model.user.Waiter;

import java.time.LocalDate;
import java.util.List;

public interface WaiterService {
    Waiter getLeastBusyWaiterAtLocation(String locationId,LocalDate date,String startTime);
    void bookWaiterSlots(Waiter waiter, LocalDate date, String time);
    void updateWaiter(Waiter waiter);
    Waiter findWaiterById(String id);
    WaiterProfileDto findWaiterProfile();
    Waiter findWaiterByEmail(String email);
    List<String> findAvailableSlots(LocalDate date);
    boolean isWaiterAvailable(Waiter waiter,LocalDate date,String time);
}
