package ru.practicum.main.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.constants.Constants;
import ru.practicum.validator.DateAfterValueHourFutureValid;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventUpdateAdminRequest extends EventUpdateRequest {

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @DateAfterValueHourFutureValid(value = Constants.TWO_AS_STRING)
    private LocalDateTime eventDate;
}