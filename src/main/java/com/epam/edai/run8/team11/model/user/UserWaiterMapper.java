package com.epam.edai.run8.team11.model.user;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.function.Function;

@Service
public class UserWaiterMapper implements Function<User, Waiter> {

    @Override
    public Waiter apply(User user) {
        Waiter waiter = new Waiter();
        waiter.setUserId(user.getUserId());
        waiter.setFirstName(user.getFirstName());
        waiter.setLastName(user.getLastName());
        waiter.setEmail(user.getEmail());
        waiter.setPassword(user.getPassword());
        waiter.setRole(user.getRole());
        waiter.setLocationId("");
        waiter.setCount(0);
        waiter.setSlots(Collections.emptyMap());
        return waiter;
    }
}
