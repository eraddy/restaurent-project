package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.user.waiter.profile.WaiterProfileDetailsDto;
import com.epam.edai.run8.team11.service.waiter.WaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/waiters")
@RequiredArgsConstructor
public class WaiterController {

    private final WaiterService waiterService;

    @GetMapping("/profile")
    public ResponseEntity<WaiterProfileDetailsDto> getWaiterProfile(){
        return ResponseEntity.ok(waiterService.findWaiterProfile());
    }
}
