package ru.practicum.main.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.api.request.RequestStatus;
import ru.practicum.validator.EnumAllowedValid;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RequestStatusUpdateRequest {

    @Size(min = 1)
    private List<Long> requestIds;

    @EnumAllowedValid(enumClass = RequestStatus.class, allowed = {"CONFIRMED", "REJECTED"})
    private RequestStatus status;
}
