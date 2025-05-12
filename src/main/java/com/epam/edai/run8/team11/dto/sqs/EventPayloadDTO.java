package com.epam.edai.run8.team11.dto.sqs;

import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;


@Data
@Builder
public class EventPayloadDTO {
    private EventType eventType;
    private LocalDate date;
    private String reservationId;
    private String waiterId;
    private Integer workingHours;
}
