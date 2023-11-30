package ru.practicum.main.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import ru.practicum.constants.Constants;
import ru.practicum.validator.DateAfterValueHourFutureValid;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
public class EventUpdateRequest {

    @Size(min = 3, max = 120)
    private String title;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    private LocationDto location;

    private Boolean paid;

    private Boolean requestModeration;

    @PositiveOrZero
    private Long participantLimit;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @DateAfterValueHourFutureValid(value = Constants.ONE_AS_STRING)
    private LocalDateTime eventDate;

    private StateAction stateAction;
}
