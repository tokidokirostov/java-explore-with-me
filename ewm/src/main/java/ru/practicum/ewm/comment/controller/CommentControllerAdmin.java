package ru.practicum.ewm.comment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@Slf4j
@RestController
@RequestMapping("admin")
@AllArgsConstructor
public class CommentControllerAdmin {
    @Autowired
    private final CommentService commentService;

    //Просотр коментария администратором
    @GetMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public CommentDto getCommentAdmin(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        log.info("---> Получен запрос GET /admin/{}/events/{}/comments/{}", userId, eventId, commentId);
        return commentService.getCommentAdmin(userId, eventId, commentId);
    }

    //Просмотр коментария администратором
    @DeleteMapping("/{userId}/events/{eventId}/comments/{commentId}")
    public void deleteCommentAdmin(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        log.info("---> Получен запрос DELETE /admin/{}/events/{}/comments/{}", userId, eventId, commentId);
        commentService.deleteCommentAdmin(userId, eventId, commentId);
    }
}
