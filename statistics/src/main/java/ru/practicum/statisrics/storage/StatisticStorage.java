package ru.practicum.statisrics.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statisrics.dto.ViewStats;
import ru.practicum.statisrics.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticStorage extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.statisrics.dto.ViewStats(s.app, s.uri, count(s.id)) from EndpointHit as s " +
            "where (s.timestamp between ?1 and ?2) and s.uri=(?3) " +
            "group by s.uri, s.app")
    List<ViewStats> findAllEndpointsUri(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uri
    );

    @Query("select new ru.practicum.statisrics.dto.ViewStats(s.app, s.uri, count(s.id)) from EndpointHit as s " +
            "where (s.timestamp between ?1 and ?2) " +
            "group by s.app, s.uri")
    List<ViewStats> findAllEndpoints(
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("select new ru.practicum.statisrics.dto.ViewStats(s.app, s.uri, count(distinct s.ip)) from EndpointHit as s " +
            "where (s.timestamp between ?1 and ?2) and s.uri=(?3) " +
            "group by s.uri, s.app")
    List<ViewStats> findAllEndpointsUriUnique(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uri
    );

    @Query("select new ru.practicum.statisrics.dto.ViewStats(s.app, s.uri, count(distinct s.ip)) from EndpointHit as s " +
            "where (s.timestamp between ?1 and ?2) " +
            "group by s.app, s.uri")
    List<ViewStats> findAllEndpointsUnique(
            LocalDateTime start,
            LocalDateTime end
    );
}
