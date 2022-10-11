package ru.practicum.statisrics.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statisrics.dto.EndpointHitDto;
import ru.practicum.statisrics.dto.ViewStats;
import ru.practicum.statisrics.service.StatisticService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class StatisticController {
    @Autowired
    private final StatisticService statisticService;

    //Сохранение статистики обращений
    @PostMapping("/hit")
    public EndpointHitDto addEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("---> Получен запрос POST /hit EndpointHitDto - {}", endpointHitDto.toString());
        return statisticService.addEndpointHit(endpointHitDto);
    }

    //Получение статистики по посещениям
    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start") String start,
                                    @RequestParam(name = "end") String end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", required = false) Boolean unique
    ) {
        log.info("---> Получен запрос Get /stats ");
        return statisticService.getStats(start, end, uris, unique);
    }
}
