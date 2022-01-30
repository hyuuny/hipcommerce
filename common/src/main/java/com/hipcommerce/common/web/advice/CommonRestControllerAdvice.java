package com.hipcommerce.common.web.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.hipcommerce.common.exceptions.UserNotFoundException;
import com.hipcommerce.common.web.model.HttpStatusMessageException;
import com.hipcommerce.common.web.model.ErrorResponse;
import com.hipcommerce.common.web.model.ErrorResponse.FieldError;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestController.class)
public class CommonRestControllerAdvice extends ResponseEntityExceptionHandler {

  private final MessageSourceAccessor messageSourceAccessor;
  private final ModelMapper modelMapper;

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request
  ) {
    BindingResult bindingResult = ex.getBindingResult();
    List<FieldError> fieldErrors = this.getFieldErrors(bindingResult);
    final String code = "exception." + ex.getClass().getSimpleName();

    return toErrorResponse(
        ex,
        status,
        code,
        messageSourceAccessor.getMessage(code),
        fieldErrors
    );
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request
  ) {
    BindingResult bindingResult = ex.getBindingResult();
    List<FieldError> fieldErrors = this.getFieldErrors(bindingResult);
    final String code = "exception." + ex.getClass().getSimpleName();

    return toErrorResponse(
        ex,
        status,
        code,
        messageSourceAccessor.getMessage(code),
        fieldErrors
    );
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      @Nullable Object body,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request
  ) {
    return toErrorResponse(
        ex,
        status,
        null,
        ex.getMessage(),
        null
    );
  }

  @ExceptionHandler({AccessDeniedException.class})
  protected ResponseEntity<Object> handleAccessDeniedException(
      final Exception ex,
      final WebRequest request
  ) {
    return toErrorResponse(
        ex,
        HttpStatus.FORBIDDEN,
        null,
        ex.getMessage(),
        null
    );
  }

  @ExceptionHandler({UserNotFoundException.class})
  protected ResponseEntity<Object> handleUserNotFoundException(
      final Exception ex
  ) {
    return toErrorResponse(
        ex,
        HttpStatus.UNAUTHORIZED,
        "user.notFound",
        ex.getMessage(),
        null
    );
  }

  @ExceptionHandler(HttpStatusCodeException.class)
  protected ResponseEntity<Object> httpStatusCodeException(
      final HttpStatusCodeException ex
  ) {
    return toErrorResponse(
        ex,
        ex.getStatusCode(),
        null,
        ex.getResponseBodyAsString(),
        null
    );
  }

  @ExceptionHandler(HttpStatusMessageException.class)
  protected ResponseEntity<Object> handleHttpStatusMessageException(HttpStatusMessageException ex) {
    final String code = ex.getCode();
    final HttpStatus status = ex.getStatus();
    final Object[] args = ex.getArgs();

    return toErrorResponse(ex, status, code, ex.getMessage(), null, args);
  }

  @ExceptionHandler({AuthenticationException.class})
  protected ResponseEntity<Object> handleAuthenticationException(
      final Exception ex,
      final WebRequest request
  ) {
    return toErrorResponse(
        ex,
        HttpStatus.UNAUTHORIZED,
        null,
        ex.getMessage(),
        null
    );
  }

  @JsonInclude(Include.NON_EMPTY)
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleException(
      Exception ex,
      HttpServletRequest request
  ) {
    HttpStatus status = this.getStatus(request);
    return toErrorResponse(ex, status, null, ex.getMessage(), null);
  }

  private ResponseEntity<Object> toErrorResponse(
      final Exception ex,
      final HttpStatus status,
      @Nullable String code,
      final String message,
      @Nullable List<FieldError> fieldErrors,
      Object... args
  ) {
    code = Objects.toString(code, "exception." + ex.getClass().getSimpleName());
    String convertMessage = null;
    try {
      convertMessage = messageSourceAccessor.getMessage(code, args);
    } catch (Exception e) {
      // log.error("No such message", ex);
    }

    log.error("Error", ex);

    return ResponseEntity
        .status(status)
        .body(
            ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(Objects.toString(convertMessage, message))
                .fieldError(fieldErrors)
                .build()
        );
  }

  private List<FieldError> getFieldErrors(BindingResult bindingResult) {
    return modelMapper.map(bindingResult.getFieldErrors(), new TypeToken<List<FieldError>>() {
    }.getType());
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.valueOf(statusCode);
  }
}
