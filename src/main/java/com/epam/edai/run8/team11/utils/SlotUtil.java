package com.epam.edai.run8.team11.utils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SlotUtil {
    public static void populateSlotMapForDate(Map<String, List<String>> slots, LocalDate date) {
        if (!slots.containsKey(date.toString())) {
            slots.put(date.toString(), getDefaultSlots());
        }
    }
    public static List<String> getDefaultSlots() {
        return List.of("10:30", "12:15", "14:00", "15:45", "17:30", "19:15", "21:00");
    }
}
