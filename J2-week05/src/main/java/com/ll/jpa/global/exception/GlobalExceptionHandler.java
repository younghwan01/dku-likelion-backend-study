package com.ll.jpa.global.exception;
import com.ll.jpa.global.rsData.RsData;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<RsData<Void>> handle(GlobalException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new RsData<>(ex.getStatus().value()+"-1", ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField()+": "+error.getDefaultMessage()).findFirst().orElse("입력값을 확인해주세요.");
        return ResponseEntity.badRequest().body(new RsData<>("400-1", message));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<RsData<Void>> malformed(Exception ex) {
        return ResponseEntity.badRequest().body(new RsData<>("400-2", "요청 형식을 확인해주세요."));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RsData<Void>> integrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new RsData<>("409-1", "중복 또는 데이터 제약조건을 확인해주세요."));
    }
}
