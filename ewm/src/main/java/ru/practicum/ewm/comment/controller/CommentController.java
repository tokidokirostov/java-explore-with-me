package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class CommentController {
    @Autowired
    private final CommentService commentService;

    //Создание коментария пользователем
    @PostMapping("/{userId}/events/{eventId}/comments")
    public CommentDto addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody NewCommentDto newCommentDto) {
        log.info("---> Получен запрос POST /users/{}/events/{} NewCommentDto - {}", userId, eventId, newCommentDto.toString());
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    //Просмотр коментрария пользователем
    @GetMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        log.info("---> Получен запрос GET /users/{}/events/{}/comments/{}", userId, eventId, commentId);
        return commentService.getComment(userId, eventId, commentId);
    }

    //Удаление коментария пользователем
    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.info("---> Получен запрос DELETE /users/{}/events/{}/comments/{}", userId, eventId, commentId);
        commentService.deleteComment(userId, eventId, commentId);
    }

    //Изменение коментария пользователем
    @PutMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto putComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody NewCommentDto newCommentDto) {
        log.info("---> Получен запрос PUT /users/{}/events/{} NewCommentDto - {}", userId, eventId, newCommentDto.toString());
        return commentService.putComment(userId, eventId, commentId, newCommentDto);
    }
}
