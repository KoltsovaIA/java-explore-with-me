package ru.practicum.server.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.api.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class EventGetAllByAdminParameters {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<Long> users;
    private List<Long> categories;
    private List<EventStatus> states;
    private Boolean onlyPending;
    private int from;
    private int size;
}
