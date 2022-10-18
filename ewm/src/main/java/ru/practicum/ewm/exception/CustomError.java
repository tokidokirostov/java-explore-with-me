package ru.practicum.ewm.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomError {
    private final String[] errors;
    private final String message;
    private final String reason;
    private final String status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp = LocalDateTime.now();

    public CustomError(String[] errors, String message, String reason, String status) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = "" + status;
    }
}
