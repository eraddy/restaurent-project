package com.epam.edai.run8.team11.exception.table;

public class TableNotFoundException extends RuntimeException {
    public TableNotFoundException(Integer tableNumber) {
        super("Table not found: " + tableNumber);
    }
}
