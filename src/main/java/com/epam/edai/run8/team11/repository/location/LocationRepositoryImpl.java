package com.epam.edai.run8.team11.repository.location;

import com.epam.edai.run8.team11.model.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;


@Repository
public class LocationRepositoryImpl implements LocationRepository{

    private final DynamoDbTable<Location> locationTable;

    public LocationRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.locationTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Location.class));
    }

    public void save(Location location) {
        locationTable.putItem(location);
    }

    public Optional<Location> findById(String locationId) {
        return Optional.ofNullable(locationTable.getItem(Key.builder().partitionValue(locationId).build()));
    }

    public List<Location> findAll(){
        return locationTable.scan().items().stream().toList();
    }

    public void deleteById(String locationId) {
        locationTable.deleteItem(r -> r.key(k -> k.partitionValue(locationId)));
    }
}