package ru.practicum.ewm.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentMapper;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.service.CommentService;
import org.mockito.Mock;
import ru.practicum.ewm.comment.storage.CommentRepository;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.storage.EventStorage;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    EventStorage eventStorage;
    @Mock
    EventService eventService;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentService commentService;

    User user = new User(1L, "user@user.com", "user");
    Category category = new Category(1L, "Action");
    Event event = new Event(1L, "Annotation", category, 5, LocalDateTime.now(),
            "Description", LocalDateTime.now().plusDays(2), user, false, 0,
            null, false, "PENDING", "Title", 0, 10, 10);
    NewCommentDto newCommentDto = new NewCommentDto("Some comment of user");
    Comment comment = new Comment(1L, "Some comment of user", user, event, LocalDateTime.now());
    CommentDto commentDto = CommentMapper.toCommentDto(comment);
    List<CommentDto> commentDtoList = new ArrayList<>();
    EventDto eventDto = EventMapper.toEventDto(event, commentDtoList);
    @Test
    void whenTryCreateCommentUser_thenReturnCommentCreated() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.of(event));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        var result = commentService.addComment(1L, 1L, newCommentDto);
        assertEquals(commentDto, result);
    }

    @Test
    void whenTryCreateCommentWithoutUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.addComment(1L, 1L, newCommentDto));
    }

    @Test
    void whenTryCreateCommentWithoutEvent_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.addComment(1L, 1L, newCommentDto));
    }

    @Test
    void whenTryGetCommentWithoutUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when(eventStorage.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.getComment(1L, 1L, 1L));
    }

    @Test
    void whenTryGetCommentWithoutEvent_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.getComment(1L, 1L, 1L));
    }

    @Test
    void whenTryGetCommentUser_thenReturnComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.of(event));
        when(eventService.getEvent(anyString())).thenReturn(eventDto);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        var result = commentService.getComment(1L, 1L, 1L);
        assertEquals(commentDto, result);
    }

    @Test
    void whenTryDeleteCommentWithoutUser_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.deleteComment(1L, 1L, 1L));
    }

    @Test
    void whenTryDeleteCommentWithoutEvent_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.deleteComment(1L, 1L, 1L));
    }

    @Test
    void whenTryDeleteCommentWithoutComment_thenReturnCustomException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.of(event));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> commentService.deleteComment(1L, 1L, 1L));
    }

    @Test
    void whenTryDeleteComment_thenReturnOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventStorage.findById(anyLong())).thenReturn(Optional.of(event));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.deleteComment(1L, 1L, 1L);
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

}
