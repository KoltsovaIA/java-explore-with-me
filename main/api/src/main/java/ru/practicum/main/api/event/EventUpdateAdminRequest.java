package ru.practicum.main.api.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class EventUpdateAdminRequest extends EventUpdateRequest {
}