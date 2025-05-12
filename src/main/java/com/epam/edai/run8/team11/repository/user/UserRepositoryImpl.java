package com.epam.edai.run8.team11.repository.user;

import com.epam.edai.run8.team11.dto.user.defaulttable.DefaultTableDto;
import com.epam.edai.run8.team11.model.user.UserWaiterMapper;
import com.epam.edai.run8.team11.model.user.Waiter;
import com.epam.edai.run8.team11.model.user.role.Role;
import com.epam.edai.run8.team11.model.user.User;
import com.epam.edai.run8.team11.exception.user.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbTable<User> userTable;
    private final DynamoDbTable<Waiter> waiterTable;
    private final DynamoDbTable<DefaultTableDto> defaultTable;
    private static final String USER_ID_INDEX_NAME = "userId-index"; // Use your actual GSI address here
    private final PasswordEncoder passwordEncoder;
    private final UserWaiterMapper userWaiterMapper;

    public UserRepositoryImpl(DynamoDbEnhancedClient enhancedClient, PasswordEncoder passwordEncoder, UserWaiterMapper userWaiterMapper) {
        this.userTable = enhancedClient.table("users",
                TableSchema.fromBean(User.class));

        this.waiterTable = enhancedClient.table("waiter_table",
                TableSchema.fromBean(Waiter.class));

        this.defaultTable = enhancedClient.table("default_users",
                TableSchema.fromBean(DefaultTableDto.class));

        this.passwordEncoder = passwordEncoder;
        this.userWaiterMapper = userWaiterMapper;
    }

    @Override
    public Optional<User> findById(String email) {
        try {
            Key key = Key.builder()
                    .partitionValue(email)
                    .build();

            User user = userTable.getItem(key);
            log.info("User Retrieved -> {}", user);

            return Optional.ofNullable(user);

        } catch (Exception e) {
            log.error("Inside findById Catch", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();

        PageIterable<User> queryResults = (PageIterable<User>) userTable.index(USER_ID_INDEX_NAME).query(queryRequest);

        Optional<User> foundUser = queryResults.items().stream().findFirst();

        return foundUser;
    }

    @Override
    public List<User> findAll() {
        return userTable.scan().items().stream().toList();
    }

    @Override
    public void save(User user) {
        Optional<User> byPartitionKey = findById(user.getEmail());
        if (byPartitionKey.isPresent()) throw new UserAlreadyExistsException(user.getEmail());

        Key key = Key.builder()
                .partitionValue(user.getEmail())
                .build();

        log.info("Key -> {}", key.partitionKeyValue().s());
        String uuid = UUID.randomUUID().toString();

        Optional<DefaultTableDto> defaultDto = getDefaultTableEntry(key);
        Role role = defaultDto.map(DefaultTableDto::getRole)
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

        Optional<User> getUser = findById(user.getEmail());
        log.info("User Retrieved -> {}", getUser);

        String userId = getUser.get().getUserId();
        log.info("User Saved with userId -> {}", userId);

    }

    @Override
    public User updateUser(User user) {
        return userTable.updateItem(user);
    }

    private Optional<DefaultTableDto> getDefaultTableEntry(Key key){
        return Optional.ofNullable(defaultTable.getItem(key));
    }

    private Role getRole(Key key){
        Optional<DefaultTableDto> item = defaultTable
                .scan().items()
                .stream()
                .filter(obj -> obj.getEmail().equalsIgnoreCase(key.partitionKeyValue().s())).findFirst();

        return item.map(defaultTableDto -> Role.valueOf(defaultTableDto.getRole().toUpperCase())).orElse(Role.CUSTOMER);

    }
}
