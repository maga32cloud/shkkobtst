package com.cafe.mobile.shcafe.common.exception;

import com.cafe.mobile.shcafe.common.model.AppResponse;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Exception 공통 처리부
    private ResponseEntity<AppResponse> createResponseEntity(HttpStatus status, String code, String message, Object data){
        return ResponseEntity.status(status).body(
                AppResponse.builder().code(code).message(message).data(data).build()
        );
    }

    private ResponseEntity<AppResponse> createResponseEntity(HttpStatus status, String code, String message){
        return createResponseEntity(status, code, message, "");
    }


    // Validate 인자값 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppResponse> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        // 오류항목, 메세지 전달
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return createResponseEntity(HttpStatus.BAD_REQUEST, ResponseType.INVALID_ARGUMENT.code(), ResponseType.INVALID_ARGUMENT.message() ,errors);
    }

    // 인자값 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppResponse> handleValidationException(HttpMessageNotReadableException exception) {
        if (exception.getCause() instanceof MismatchedInputException mismatchedInputException) {
            log.debug("잘못된 파라미터 : ", exception);
            // 오류항목, 메세지 전달
            return createResponseEntity(HttpStatus.BAD_REQUEST, ResponseType.INVALID_ARGUMENT.code(), ResponseType.INVALID_ARGUMENT.message()
                    , mismatchedInputException.getPath().get(0).getFieldName()
            );
        }
        return createResponseEntity(HttpStatus.BAD_REQUEST, ResponseType.INVALID_ARGUMENT.code(), ResponseType.INVALID_ARGUMENT.message());
    }

    // 중복값 오류
    @ExceptionHandler({DuplicateKeyException.class, AlreadyExistsException.class})
    public ResponseEntity<AppResponse> handleDuplicateKeyException(DuplicateKeyException exception) {
        String message = (exception instanceof AlreadyExistsException) ? exception.getMessage() : ResponseType.DUP_KEY.message();
        return createResponseEntity(HttpStatus.CONFLICT, ResponseType.DUP_KEY.code(), message);
    }

    // 비즈니스 예외
    @ExceptionHandler(BizException.class)
    public ResponseEntity<AppResponse> handleBizException(BizException exception) {
        log.debug("BIZ 예외 발생 : ", exception);
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getCode(), exception.getMessage());
    }
}
