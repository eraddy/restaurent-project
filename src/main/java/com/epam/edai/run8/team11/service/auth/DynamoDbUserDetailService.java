package com.epam.edai.run8.team11.service.auth;

import com.epam.edai.run8.team11.dto.RepositoryBodyDto;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
public class DynamoDbUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RepositoryBodyDto<Optional<User>> userResult = userRepository.getByPartitionKey(username);
        if(!userResult.isSuccess() && !userResult.getData().isPresent()){
            throw new UsernameNotFoundException(userResult.getMessage());
        }

        User user = userResult.getData().get();
        return new DynamoDbUserPrincipal() {
            @Override
            public String getEmail() {
                return user.getEmail();
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString()));
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getFirstName() + " " + user.getLastName();
            }
        };
    }
}
