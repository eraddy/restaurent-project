package com.epam.edai.run8.team11.service.table;

import com.epam.edai.run8.team11.model.table.Table;
import com.epam.edai.run8.team11.model.table.response.TableResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface TableService {
    List<TableResponseDto> getAvailableTables(String locationId, String date, int guestsNumber, String time);
    boolean isTableAvailable(Table table, LocalDate date, String timeFrom);
    void bookTableSlots(Table table, LocalDate date, String time);
    List<Table> findAll();
    List<Table> findById(String locationID);
    Table findByIdAndNumber(String locationId, Integer tableNumber);
    void updateTable(Table table);
}
