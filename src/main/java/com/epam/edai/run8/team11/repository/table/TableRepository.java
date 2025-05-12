package com.epam.edai.run8.team11.repository.table;

import com.epam.edai.run8.team11.model.table.Table;

import java.util.List;

public interface TableRepository {
    List<Table> findAll();
    List<Table> findById(String locationID);
    Table findFyIdAndNumber(String locationId,Integer tableNumber);
    void update(Table table);
}
