package ru.practicum.ewm.reository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.ParamDto;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.mapper.StatsResponseDtoRowMapper;
import ru.practicum.ewm.model.Stats;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsDbRepository implements StatsRepository{
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final StatsResponseDtoRowMapper dtoRowMapper;
    private static final String INSERT_HIT = "INSERT INTO stats (app, uri, ip, times) VALUES (?, ?, ?, ?)";
    public static final String GET_STAT = """
            SELECT app, uri,
            CASE WHEN :unique = true THEN COUNT(DISTINCT ip) ELSE COUNT(*) END as hits
            FROM stats
            WHERE times BETWEEN :start AND :end
            AND (:uris IS NULL OR uri IN (:uris))
            GROUP  BY app, uri
            ORDER BY hits DESC
            """;

    @Override
    public Stats addHit(Stats hit) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_HIT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, hit.getApp());
            ps.setString(2, hit.getUri());
            ps.setString(3, hit.getIp());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(hit.getTimestamp()));
            return ps;
        }, key);

        if (key.getKey() == null) {
            throw new IllegalStateException("Не сгенерировался ключ новой записи.");
        }

        long id = key.getKey().longValue();
        hit.setId(id);
        return hit;
    }

    @Override
    public List<StatsResponseDto> getStats(ParamDto param) {
        return namedJdbc.query(GET_STAT, new MapSqlParameterSource()
                .addValue("unique", param.isUnique())
                .addValue("start", param.getStart())
                .addValue("end", param.getEnd())
                .addValue("uris", param.getUris()), dtoRowMapper);
    }
}
