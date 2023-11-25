package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.IncomingDto;
import ru.practicum.client.StatClient;
import ru.practicum.validator.StartBeforeEndDateValid;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@Controller
@RequiredArgsConstructor
@Validated
public class StatClientController {
    private final StatClient client;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createStat(@RequestBody @Valid IncomingDto incomingDto) {
        return client.postStat(incomingDto);
    }

    @GetMapping("/stats")
    @StartBeforeEndDateValid
    public ResponseEntity<Object> getStat(@RequestParam(name = "start") @DateTimeFormat(fallbackPatterns =
            DATE_TIME_FORMAT) LocalDateTime start,
                                          @RequestParam(name = "end") @DateTimeFormat(fallbackPatterns =
                                                  DATE_TIME_FORMAT) LocalDateTime end,
                                          @RequestParam(name = "uris", required = false) List<String> uris,
                                          @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return client.getStat(start, end, uris, unique);
    }
}