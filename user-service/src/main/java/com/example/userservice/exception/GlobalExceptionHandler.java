package com.example.userservice.exception;

import com.cursor.common.dto.BaseResponse;
import com.cursor.common.enum_.StatusEnum;
import com.cursor.common.exception.BusinessException;
import com.cursor.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleBusiness(BusinessException ex) {
        return BaseResponse.<Void>builder()
                .status(StatusEnum.ERROR)
                .code(ex.getErrorCode().name())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation error");
        return BaseResponse.<Void>builder()
                .status(StatusEnum.ERROR)
                .code(ErrorCode.VALIDATION_ERROR.name())
                .message(message)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<Void> handleUnknown(Exception ex) {
        return BaseResponse.<Void>builder()
                .status(StatusEnum.ERROR)
                .code(ErrorCode.UNKNOWN_ERROR.name())
                .message("Unexpected error")
                .build();
    }
}
