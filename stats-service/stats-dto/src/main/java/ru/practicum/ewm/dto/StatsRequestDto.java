package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsRequestDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
