package com.epam.edai.run8.team11.repository.dish;

import com.epam.edai.run8.team11.model.dish.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;

@Repository
public class DishRepositoryImpl implements DishRepository {

    private final DynamoDbTable<Dish> dishTable;

    public DishRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dishTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Dish.class));
    }

    public Dish save(Dish dish) {
        dishTable.putItem(dish);
        return dish;
    }

    public Optional<Dish> findById(String dishId) {
        return findAll().stream().filter(dish -> dish.getDishId().equals(dishId)).findFirst();
    }

    @Override
    public List<Dish> findAll() {
        return dishTable.scan().items().stream().toList();
    }

    @Override
    public List<Dish> findDishesByLocationId(String locationId) {
        return findAll().stream()
                .filter(dish -> dish.getLocationId().equals(locationId))
                .toList();
    }

    @Override
    public List<Dish> findSpecialityDishesByLocationId(String locationId) {
        return findDishesByLocationId(locationId).stream()
                .filter(Dish::getIsSpeciality)
                .toList();
    }

    public void deleteById(String dishId) {
        dishTable.deleteItem(r -> r.key(k -> k.partitionValue(dishId)));
    }
}