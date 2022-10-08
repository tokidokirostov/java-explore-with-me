package ru.practicum.ewm.admin.compilations.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.service.CompilationService;

@Slf4j
@RestController
@RequestMapping(path = "admin/compilations")
@AllArgsConstructor
public class CompilationController {
    @Autowired
    private final CompilationService compilationService;

    //Добавление подборки
    @PostMapping
    public CompilationDto addCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        log.info("---> Получен запрос POST /admin/compilations categories - {}", newCompilationDto.toString());
        return compilationService.addCompilation(newCompilationDto);
    }

    //Удаление подборки
    @DeleteMapping("{compId}")
    public void deleteCompilation(@PathVariable String compId) {
        log.info("---> Получен запрос DELETE admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
    }

    //Удаление события из подборки
    @DeleteMapping("{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable String compId,
                                           @PathVariable String eventId) {
        log.info("---> Получен запрос DELETE admin/compilations/{}/events/{}", compId, eventId);
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    //Добавить событие в подборку
    @PatchMapping("{compId}/events/{eventId}")
    public void addEventToCompilation(@PathVariable String compId,
                                      @PathVariable String eventId) {
        log.info("---> Получен запрос PATCH admin/compilations/{}/events/{}", compId, eventId);
        compilationService.addEventToCompilation(compId, eventId);
    }

    //Закрепить подборку на главной странице
    @PatchMapping("{compId}/pin")
    public void addCompilationOnBoard(@PathVariable String compId) {
        log.info("---> Получен запрос PATCH admin/compilations/{}/pin", compId);
        compilationService.addCompilationOnBoard(compId);
    }

    //Открепить подборку на главной странице
    @DeleteMapping("{compId}/pin")
    public void deleteCompilationOnBoard(@PathVariable String compId) {
        log.info("---> Получен запрос DELETE admin/compilations/{}/pin", compId);
        compilationService.deleteCompilationOnBoard(compId);
    }
}
