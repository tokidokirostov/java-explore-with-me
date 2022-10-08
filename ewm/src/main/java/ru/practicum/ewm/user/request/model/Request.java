
package ru.practicum.ewm.user.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.admin.user.model.User;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.model.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class Request {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    Event event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "requester_id")
    User requester;
    EventState status;
}
