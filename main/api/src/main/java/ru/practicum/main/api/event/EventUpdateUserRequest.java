package ru.practicum.main.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.constants.Constants;
import ru.practicum.validator.DateAfterValueHourFutureValid;
import ru.practicum.validator.EnumAllowedValid;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventUpdateUserRequest extends EventUpdateRequest {

    @EnumAllowedValid(enumClass = StateAction.class, allowed = {"SEND_TO_REVIEW", "CANCEL_REVIEW"})
    private StateAction stateAction;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @DateAfterValueHourFutureValid(value = Constants.ONE_AS_STRING)
    private LocalDateTime eventDate;
}
