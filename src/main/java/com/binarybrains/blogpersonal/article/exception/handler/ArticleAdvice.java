package com.binarybrains.blogpersonal.article.exception.handler;

import static com.binarybrains.blogpersonal.common.ApiConstants.MESSAGE_ERROR;
import static com.binarybrains.blogpersonal.common.ApiConstants.VALIDATION_ERROR;

import com.binarybrains.blogpersonal.article.exception.custom.ArticleNotFoundException;
import com.binarybrains.blogpersonal.common.ErrorResponse;
import com.binarybrains.blogpersonal.common.FieldsErrorResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ArticleAdvice {

  @ExceptionHandler(ArticleNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleArticleNotFoundException(ArticleNotFoundException ex) {

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .message(ex.getMessage())
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<FieldsErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

    FieldsErrorResponse fieldsErrorResponse =
        FieldsErrorResponse.builder()
            .error(VALIDATION_ERROR)
            .message(MESSAGE_ERROR)
            .fields(fieldErrors)
            .build();

    return ResponseEntity.badRequest().body(fieldsErrorResponse);
  }
}
