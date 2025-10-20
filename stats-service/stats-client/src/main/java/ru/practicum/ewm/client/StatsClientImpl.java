package ru.practicum.ewm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class StatsClientImpl implements StatsClient {
    private final RestTemplate rest;
    private final String serverUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String HIT_ENDPOINT = "/hit";
    private static final String STATS_ENDPOINT = "/stats";

    public StatsClientImpl(@Value("${stats.server.url}") String serverUrl, RestTemplateBuilder restBuilder) {
        this.serverUrl = serverUrl;
        this.rest = restBuilder.build();
    }

    @Override
    public void saveHit(StatsRequestDto dto) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(serverUrl)
                    .path(HIT_ENDPOINT);
            HttpEntity<StatsRequestDto> entity = new HttpEntity<>(dto, defaultHeaders());
            rest.postForEntity(uriBuilder.toUriString(), entity, Void.class);
        } catch (RestClientException e) {
            log.warn("Ошибка при сохранении статистики: {}", e.getMessage());
        }
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, List<String> uris, boolean unique) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromHttpUrl(serverUrl)
                    .path(STATS_ENDPOINT)
                    .queryParam("start", start)
                    .queryParam("end", end);

            if (uris != null && !uris.isEmpty()) {
                uriBuilder.queryParam("uris", uris);
            }

            uriBuilder.queryParam("unique", unique);

            return rest.exchange(uriBuilder.toUriString(), HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                    new ParameterizedTypeReference<List<StatsResponseDto>>() {
                    }).getBody();
        } catch (RestClientException e) {
            log.warn("Ошибка при получении данных от сервера статистики: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
