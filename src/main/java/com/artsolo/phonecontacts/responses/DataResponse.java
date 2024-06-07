package com.artsolo.phonecontacts.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DataResponse<T> {
    private boolean success;
    private int code;
    private T data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    public DataResponse(boolean success, int code, T data) {
        this.success = success;
        this.code = code;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
