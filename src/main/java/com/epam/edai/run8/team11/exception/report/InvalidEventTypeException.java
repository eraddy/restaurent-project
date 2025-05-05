package com.epam.edai.run8.team11.exception.report;

import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;

import java.util.Arrays;

public class InvalidEventTypeException extends RuntimeException{
    public InvalidEventTypeException(){
        super("Invalid event type, allowed event types: " + Arrays.toString(EventType.values()));
    }
}
