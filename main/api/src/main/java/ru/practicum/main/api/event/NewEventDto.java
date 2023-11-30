package ru.practicum.main.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.constants.Constants;
import ru.practicum.validator.DateAfterValueHourFutureValid;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class NewEventDto {

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Valid
    private LocationDto location;

    @NotNull
    private Boolean paid = false;

    @NotNull
    private Boolean requestModeration = true;

    @PositiveOrZero
    private long participantLimit = 0L;

    @NotNull
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @DateAfterValueHourFutureValid(value = Constants.TWO_AS_STRING)
    private LocalDateTime eventDate;
}
