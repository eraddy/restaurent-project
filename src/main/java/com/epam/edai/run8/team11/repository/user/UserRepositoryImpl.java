package com.epam.edai.run8.team11.repository.user;

import com.epam.edai.run8.team11.dto.RepositoryBodyDto;
import com.epam.edai.run8.team11.dto.user.DefaultDto;
import com.epam.edai.run8.team11.model.user.UserWaiterMapper;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.exception.user.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbTable<User> userTable;
    private final DynamoDbTable<Waiter> waiterTable;
    private final DynamoDbTable<DefaultDto> defaultTable;
    private static final String USER_ID_INDEX_NAME = "userId-index"; // Use your actual GSI name here
    private final PasswordEncoder passwordEncoder;
    private final UserWaiterMapper userWaiterMapper;

    public UserRepositoryImpl(DynamoDbEnhancedClient enhancedClient, PasswordEncoder passwordEncoder, UserWaiterMapper userWaiterMapper) {
        this.userTable = enhancedClient.table("users",
                TableSchema.fromBean(User.class));

        this.waiterTable = enhancedClient.table("waiter_table",
                TableSchema.fromBean(Waiter.class));

        this.defaultTable = enhancedClient.table("default_users",
                TableSchema.fromBean(DefaultDto.class));

        this.passwordEncoder = passwordEncoder;
        this.userWaiterMapper = userWaiterMapper;
    }

    @Override
    public RepositoryBodyDto<Optional<User>> getByPartitionKey(String email) {
        try {
            Key key = Key.builder()
                    .partitionValue(email)
                    .build();

            User user = userTable.getItem(key);
            log.info("User Retrieved -> {}", user);

            if (user != null) {
                return RepositoryBodyDto.success(Optional.of(user), "User retrieved successfully");
            } else {
                return RepositoryBodyDto.success(Optional.empty(), "No user found with email: " + email);
            }
        } catch (Exception e) {
            log.error("Inside getByPartitionKey Catch", e);
            return RepositoryBodyDto.error("Error retrieving user by email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RepositoryBodyDto<Void> save(User user) {
        try {
            RepositoryBodyDto<Optional<User>> byPartitionKey = getByPartitionKey(user.getEmail());
            if (byPartitionKey.getData().isPresent()) throw new UserAlreadyExistsException(user.getEmail());

            Key key = Key.builder()
                    .partitionValue(user.getEmail())
                    .build();

            log.info("Key -> {}", key.partitionKeyValue().s());
            String uuid = UUID.randomUUID().toString();

            Optional<DefaultDto> defaultDto = getDefaultTableEntry(key);
            Role role = defaultDto.map(DefaultDto::getRole)
                    .map(String::toUpperCase)
                    .map(Role::valueOf).orElse(Role.CUSTOMER);
            log.info("Role -> {}", role);

            user.setRole(role);
            user.setUserId(uuid);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if(role.equals(Role.WAITER)){
                Waiter waiter = userWaiterMapper.apply(user);
                waiter.setLocationId(defaultDto.get().getLocationId());
                waiterTable.putItem(waiter);
            }

            userTable.putItem(user);

            RepositoryBodyDto<Optional<User>> getUser = getByPartitionKey(user.getEmail());
            log.info("User Retrieved -> {}", getUser.getData());

            String userId = getUser.getData().get().getUserId();
            log.info("User Saved with userId -> {}", userId);
            return RepositoryBodyDto.success("User registered successfully");
        } catch (UserAlreadyExistsException ex) {
            return RepositoryBodyDto.error("A user with this email address already exists.", HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Inside Save Catch: ", e);
            return RepositoryBodyDto.error("Error saving user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RepositoryBodyDto<Optional<User>> getByUserId(String userId) {
        try {
            // Using the GSI to efficiently query by userId
            Key key = Key.builder()
                    .partitionValue(userId)
                    .build();

            // Query the GSI directly
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key))
                    .build();

            // Execute the query against the GSI
            PageIterable<User> queryResults = (PageIterable<User>) userTable.index(USER_ID_INDEX_NAME).query(queryRequest);

            // Get the first matching user (should be only one since userId should be unique)
            Optional<User> foundUser = queryResults.items().stream().findFirst();

            if (foundUser.isPresent()) {
                return RepositoryBodyDto.success(foundUser, "User retrieved successfully");
            } else {
                return RepositoryBodyDto.success(Optional.empty(), "No user found with userId: " + userId);
            }
        } catch (Exception e) {
            log.error("Inside getByUserId Catch", e);
            return RepositoryBodyDto.error("Error retrieving user by userId: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Optional<DefaultDto> getDefaultTableEntry(Key key){
        return Optional.ofNullable(defaultTable.getItem(key));
    }

    private Role getRole(Key key){
        Optional<DefaultDto> item = defaultTable
                .scan().items()
                .stream()
                .filter(obj -> obj.getEmail().equalsIgnoreCase(key.partitionKeyValue().s())).findFirst();

        return item.map(defaultDto -> Role.valueOf(defaultDto.getRole().toUpperCase())).orElse(Role.CUSTOMER);

    }
}
