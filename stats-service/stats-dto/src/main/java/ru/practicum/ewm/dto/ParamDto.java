package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ParamDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris = new ArrayList<>();
    private boolean unique;
}
