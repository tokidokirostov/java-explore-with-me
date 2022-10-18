package ru.practicum.ewm.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.storage.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.storage.EventStorage;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CommentService {
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventStorage eventStorage;

    @Autowired
    private final EventService eventService;

    //Создание коментария пользователем
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = isUser(userId);
        Event event = isEvent(eventId);
        Comment comment = commentRepository.save(CommentMapper.toComment(newCommentDto, user, event));
        return CommentMapper.toCommentDto(comment);
    }

    //Просмотр коментрария пользователем
    public CommentDto getComment(Long userId, Long eventId, Long commentId) {
        isUser(userId);
        Event event = isEvent(eventId);
        event.setViews(eventService.getEvent(String.valueOf(eventId)).getViews());
        Comment comment = getCommentInBase(commentId);
        comment.setEvent(event);
        return CommentMapper.toCommentDto(comment);
    }

    //Удаление коментария пользователем
    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        User user = isUser(userId);
        isEvent(eventId);
        Comment comment = getCommentInBase(commentId);
        if (comment.getUser().equals(user)) {
            commentRepository.deleteById(commentId);
        } else throw new ForbiddenError(String.format("FORBIDDEN"));
    }

    //Изменение коментария пользователем
    @Transactional
    public CommentDto putComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        User user = isUser(userId);
        isEvent(eventId);
        Comment comment = getCommentInBase(commentId);
        if (comment.getUser().equals(user)) {
            comment.setComment(newCommentDto.getComment());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else throw new ForbiddenError(String.format("FORBIDDEN"));
    }

    //Просотр коментария администратором
    public CommentDto getCommentAdmin(Long userId, Long eventId, Long commentId) {
        isUser(userId);
        isEvent(eventId);
        return CommentMapper.toCommentDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", commentId))));
    }

    //Просмотр коментария администратором
    @Transactional
    public void deleteCommentAdmin(Long userId, Long eventId, Long commentId) {
        isUser(userId);
        isEvent(eventId);
        commentRepository.deleteById(commentId);
    }

    private Comment getCommentInBase(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", commentId)));
    }

    private User isUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", userId)));
    }

    private Event isEvent(Long eventId) {
        return eventStorage.findById(eventId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventId)));
    }

}
