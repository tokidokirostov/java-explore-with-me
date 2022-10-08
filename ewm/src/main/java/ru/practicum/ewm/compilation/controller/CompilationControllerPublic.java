package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilation.servise.CompilationServicePublic;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
public class CompilationControllerPublic {
    @Autowired
    private final CompilationServicePublic compilationServicePublic;

    //Получение подборок событий
    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) String pinned,
                                                   @RequestParam(defaultValue = "0") String from,
                                                   @RequestParam(defaultValue = "10") String size) {
        log.info("Получен запрос GET /compilations");
        return compilationServicePublic.getAllCompilations(pinned, from, size);
    }

    //Получение подборки событий по его id
    @GetMapping("{id}")
    public CompilationDto getCompilation(@PathVariable(name = "id") String id) {
        log.info("Получен запрос GET /compilations/{}", id);
        return compilationServicePublic.getCompilation(id);
    }
}
