package com.rpdpymnt.reportingapi.exception;

import com.rpdpymnt.reportingapi.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.EnumSet;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {


    private static final EnumSet<HttpStatus> HTTP_STATUSES_DEBUG_LOGGING = EnumSet.of(HttpStatus.NOT_FOUND);


    @ExceptionHandler(ServerResponseException.class)
    public ResponseEntity<Object> serverResponseException(ServerResponseException e, WebRequest request) {
        return handleError(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<Object> tokenGenerationException(TokenGenerationException e, WebRequest request) {
        return handleError(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGenericException(Exception e, WebRequest request) {
        return handleError(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        Object bodyObject = body != null ? body : new ApiErrorResponse((HttpStatus) statusCode, ex.getMessage());
        log(ex, request, (HttpStatus) statusCode);
        return super.handleExceptionInternal(ex, bodyObject, headers, statusCode, request);
    }

    private ResponseEntity<Object> handleError(HttpStatus httpStatus, Exception ex, WebRequest request) {
        var apiError = new ApiErrorResponse(httpStatus, ex.getMessage() );
        return handleExceptionInternal(ex, apiError, new HttpHeaders(), httpStatus, request);
    }

    private void log(Exception ex, WebRequest request, HttpStatus httpStatus) {
        String path = request.getDescription(false).replace("uri=", "");
        String message = "%s. path:%s, httpStatus:%s".formatted(ex.getMessage(), path, httpStatus.value());

        if (HTTP_STATUSES_DEBUG_LOGGING.contains(httpStatus)) {
            log.debug(message, ex);
        } else if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus)) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

}
