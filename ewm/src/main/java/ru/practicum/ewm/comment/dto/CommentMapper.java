package ru.practicum.ewm.comment.dto;

import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(null,
                newCommentDto.getComment(),
                user,
                event,
                LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getComment(),
                UserMapper.toUserShortDto(comment.getUser()),
                EventMapper.toEventShortDto(comment.getEvent()),
                comment.getCreated()

        );
    }
}
