package com.epam.edai.run8.team11.repository.table;

import com.epam.edai.run8.team11.model.Table;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class TableRepositoryImpl implements TableRepository{

    private final DynamoDbTable<Table> tablesTable;
    public static final String TABLE_NAME = "tables";

    public TableRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.tablesTable = dynamoDbEnhancedClient.table( TABLE_NAME,
                TableSchema.fromBean(Table.class));
    }

    @Override
    public List<Table> findAll() {
        return tablesTable.scan().items().stream().toList();
    }

    @Override
    public List<Table> findById(String locationID) {
        List<Table> tables = findAll();
        return tables.stream().filter(table -> table.getLocationId().equals(locationID)).collect(Collectors.toList());
    }

    @Override
    public Table findFyIdAndNumber(String locationId, Integer tableNumber) {
        return findById(locationId).stream().filter(table -> Objects.equals(table.getTableNumber(), tableNumber)).findAny().get();
    }

    @Override
    public void update(Table table) {
        tablesTable.updateItem(table);
    }


}
