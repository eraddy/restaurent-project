package com.epam.edai.run8.team11.repository.waiter;

import com.epam.edai.run8.team11.model.user.Waiter;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class WaiterRepositoryImpl implements WaiterRepository{

    private final DynamoDbTable<Waiter> waiterTable;

    public WaiterRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.waiterTable = dynamoDbEnhancedClient.table("waiter_table", TableSchema.fromBean(Waiter.class));
    }

    @Override
    public List<Waiter> getAllWaiterAtLocation(String locationId) {
        return waiterTable.scan().items().stream().filter(waiter -> waiter.getLocationId().equalsIgnoreCase(locationId)).collect(Collectors.toList());
    }

    @Override
    public void update(Waiter waiter) {
        waiterTable.updateItem(waiter);
    }

    @Override
    public Waiter findByID(String id) {
        return waiterTable.scan().items().stream().filter(waiter -> id.equals(waiter.getUserId())).findAny().get();
    }
}
