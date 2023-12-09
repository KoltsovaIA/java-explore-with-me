package ru.practicum.main.api.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.api.category.CategoryDto;
import ru.practicum.main.api.user.UserShortDto;
import ru.practicum.constants.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
    private Long participantLimit;
    private Long confirmedRequests;
    private Long views;
    private String state;
    private String moderatorComment;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime eventDate;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime publishedOn;
}
