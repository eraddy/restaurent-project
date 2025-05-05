package com.epam.edai.run8.team11.service.auth;

import org.springframework.security.core.userdetails.UserDetails;

public interface DynamoDbUserPrincipal extends UserDetails {
    String getEmail();
}
