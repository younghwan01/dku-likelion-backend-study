package com.ll.jpa.global.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class GlobalException extends RuntimeException {
    private final HttpStatus status;
    public GlobalException(HttpStatus status, String message) { super(message); this.status = status; }
}
