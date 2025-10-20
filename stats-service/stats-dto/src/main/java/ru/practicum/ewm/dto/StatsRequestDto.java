package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class StatsRequestDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
