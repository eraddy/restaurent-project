package com.epam.edai.run8.team11.service.waiter;

import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.dto.user.waiter.profile.WaiterProfileDetailsDto;
import com.epam.edai.run8.team11.dto.user.waiter.profile.WaiterProfileDetailsDtoMapper;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.repository.waiter.WaiterRepository;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import com.epam.edai.run8.team11.utils.SlotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WaiterServiceImpl implements WaiterService {

    @Autowired
    private WaiterRepository waiterRepository;
    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    private WaiterProfileDetailsDtoMapper waiterProfileDetailsDtoMapper;

    @Override
    public Waiter getLeastBusyWaiterAtLocation(String locationId, LocalDate date, String startTime) {
        log.debug("Fetching least busy waiter for locationId: {}, date: {}, startTime: {}", locationId, date, startTime);

        List<Waiter> waiters = waiterRepository.getAllWaiterAtLocation(locationId);
        if (waiters.isEmpty()) {
            log.error("No waiters found for locationId: {}", locationId);
            throw new WaiterNotFoundException("No waiter at the location");
        }

        log.info("Total waiters for locationId {}: {}", locationId, waiters.size());

        return waiters.stream()
                .filter(waiter -> isWaiterAvailable(date, startTime, waiter.getSlots()))
                .min(Comparator.comparingInt(Waiter::getCount))
                .orElseThrow(() -> {
                    log.error("No available waiter found for location {} at time {}", locationId, startTime);
                    return new WaiterNotFoundException("No waiter available for scheduling");
                });
    }

    public void bookWaiterSlots(Waiter waiter, LocalDate date, String time) {
        log.debug("Booking slot for waiter {} (ID: {}) on date {} at time {}", waiter.getFirstName(), waiter.getUserId(), date, time);

        // Retrieve the slots for the given date
        List<String> slots = waiter.getSlots().get(date.toString());
        if (slots != null) {
            // Make the list modifiable if it's immutable
            List<String> modifiableSlots = new ArrayList<>(slots);

            // Perform the modification
            if (modifiableSlots.remove(time)) {
                log.info("Successfully removed slot {} for waiter {} on date {}", time, waiter.getUserId(), date);
            } else {
                log.warn("Slot {} not found for waiter {} on date {}", time, waiter.getUserId(), date);
            }

            // Update the slots map with the modified list
            waiter.getSlots().put(date.toString(), modifiableSlots);
            log.info("Updated slots for waiter {} on date {}: {}", waiter.getUserId(), date, modifiableSlots);

        } else {
            log.warn("No slots found for waiter {} on date {}", waiter.getUserId(), date);
        }
    }

    @Override
    public void updateWaiter(Waiter waiter) {
        waiterRepository.update(waiter);
    }

    @Override
    public Waiter findWaiterById(String id) {
        return waiterRepository.findById(id)
                .orElseThrow(() -> new WaiterNotFoundException(id));
    }

    @Override
    public WaiterProfileDetailsDto findWaiterProfile() {
        UserDto userDto = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);
        Waiter waiter = findWaiterByEmail(userDto.getEmail());
        return waiterProfileDetailsDtoMapper.apply(waiter);
    }

    @Override
    public Waiter findWaiterByEmail(String email) {
        return waiterRepository.findByEmail(email).orElseThrow(() -> new WaiterNotFoundException(email));
    }

    @Override
    public List<String> findAvailableSlots(LocalDate date) {
        String id = authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new).getUserId();
        return findWaiterById(id).getSlots().getOrDefault(date.toString(), SlotUtil.getDefaultSlots());
    }

    @Override
    public boolean isWaiterAvailable(Waiter waiter, LocalDate date, String time) {
        SlotUtil.populateSlotMapForDate(waiter.getSlots(),date);
        return waiter.getSlots().get(date.toString()).contains(time);
    }

    private boolean isWaiterAvailable(LocalDate date, String timeFrom, Map<String, List<String>> waiterSlots) {
        log.debug("Checking availability for waiter on date {} for time {}", date, timeFrom);

        SlotUtil.populateSlotMapForDate(waiterSlots, date);
        List<String> slots = waiterSlots.get(date.toString());
        if (slots == null || !slots.contains(timeFrom)) {
            log.info("Waiter is unavailable for date {} at time {}", date, timeFrom);
            return false;
        }
        log.info("Waiter is available for date {} and time {}", date, timeFrom);
        return true;
    }

}