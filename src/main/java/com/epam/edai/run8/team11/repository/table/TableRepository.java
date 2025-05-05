package com.epam.edai.run8.team11.repository.table;

import com.epam.edai.run8.team11.model.Table;
import software.amazon.awssdk.services.sts.endpoints.internal.Value;

import java.util.List;

public interface TableRepository {
    List<Table> findAll();
    List<Table> findById(String locationID);
    Table findFyIdAndNumber(String locationId,Integer tableNumber);
    void update(Table table);
}
