package ru.practicum.ewm.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.StatsResponseDto;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StatsResponseDtoRowMapper implements RowMapper<StatsResponseDto> {
    @Override
    public StatsResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StatsResponseDto.builder()
                .app(rs.getString("app"))
                .uri(rs.getString("uri"))
                .hits(rs.getInt("hits"))
                .build();
    }
}
