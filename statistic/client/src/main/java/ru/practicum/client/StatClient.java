package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.IncomingDto;
import ru.practicum.OutgoingDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_TIME_FORMATTER;

public class StatClient {
    private final WebClient webClient;
    private final String appName;

    public StatClient(String serverUrl, String appName) {
        this.webClient = WebClient.create(serverUrl);
        this.appName = appName;
    }

    public void postStat(HttpServletRequest httpServletRequest) {
        IncomingDto hitDto = IncomingDto.builder()
                .app(appName)
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/hit")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(IncomingDto.class)
                .block();
    }

    public List<OutgoingDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(DATE_TIME_FORMATTER))
                        .queryParam("end", end.format(DATE_TIME_FORMATTER))
                        .queryParam("uris", String.join(", ", uris))
                        .queryParam("unique", unique.toString())
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(OutgoingDto.class)
                .collectList()
                .block();
    }
}