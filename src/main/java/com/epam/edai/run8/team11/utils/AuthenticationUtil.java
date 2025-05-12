package com.epam.edai.run8.team11.utils;

import com.epam.edai.run8.team11.dto.ServiceBodyDto;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.dto.user.waiter.WaiterUserDtoMapper;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.repository.waiter.WaiterRepository;
import com.epam.edai.run8.team11.service.auth.DynamoDbUserPrincipal;
import com.epam.edai.run8.team11.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AuthenticationUtil {

    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private WaiterRepository waiterRepository;
    @Autowired
    private WaiterUserDtoMapper waiterUserDtoMapper;

    /**
     * Gets the authenticated user's details from the security context.
     *
     * @return Optional containing UserDto if the user is authenticated, empty Optional otherwise
     */
    public Optional<UserDto> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof DynamoDbUserPrincipal userPrincipal) {

            String email = userPrincipal.getEmail();
            log.info("Current Role -> {}", userPrincipal.getAuthorities());

            log.debug("Retrieving authenticated user with email: {}", email);

            log.info("Fetching waiter table");
            Optional<Waiter> waiterOptional = waiterRepository.findByEmail(email);
            if(waiterOptional.isPresent()){
                return Optional.ofNullable(waiterUserDtoMapper.apply(waiterOptional.get()));
            }

            log.info("Fetching user table");
            ServiceBodyDto<Optional<UserDto>> userResult = userService.getUserByPartitionKey(email);

            if (userResult.isSuccess()) {
                return userResult.getData();
            } else {
                log.error("Failed to retrieve user data: {}", userResult.getMessage());
            }
        } else {
            log.debug("No authenticated user found in security context");
        }

        return Optional.empty();
    }
}