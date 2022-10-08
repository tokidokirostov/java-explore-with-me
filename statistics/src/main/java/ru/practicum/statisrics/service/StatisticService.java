package ru.practicum.statisrics.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.statisrics.dto.EndpointHitDto;
import ru.practicum.statisrics.dto.EndpointHitMapper;
import ru.practicum.statisrics.dto.ViewStats;
import ru.practicum.statisrics.storage.StatisticStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticService {
    @Autowired
    StatisticStorage statisticStorage;

    //Сохранение статистики обращений
    public EndpointHitDto addEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toEndpointHitDto(statisticStorage.save(EndpointHitMapper.toEndpointHit(endpointHitDto)));
    }

    //Получение статистики по посещениям
    public List<ViewStats> getStats(String startS, String endS, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endS, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<ViewStats> viewStats;
        if (uris.isEmpty() && unique) {
            viewStats = statisticStorage.findAllEndpointsUnique(start, end);
        } else if (uris.isEmpty() && !unique) {
            viewStats = statisticStorage.findAllEndpoints(start, end);
        } else if (!uris.isEmpty() && unique) {
            viewStats = statisticStorage.findAllEndpointsUriUnique(start, end, uris);
        } else {
            viewStats = statisticStorage.findAllEndpointsUri(start, end, uris);
        }
        return viewStats;
    }

}
