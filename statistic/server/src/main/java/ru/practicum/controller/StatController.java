package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.IncomingDto;
import ru.practicum.OutgoingDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStat(@RequestBody IncomingDto incomingDto) {
        service.createStat(incomingDto);
    }

    @GetMapping("/stats")
    public List<OutgoingDto> getStat(@RequestParam(name = "start")
                                     @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                     @RequestParam(name = "end")
                                     @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                     @RequestParam(name = "uris", required = false) List<String> uris,
                                     @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        return service.getStat(start, end, uris, unique);
    }
}