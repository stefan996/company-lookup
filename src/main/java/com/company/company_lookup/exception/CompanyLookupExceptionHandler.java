package com.company.company_lookup.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CompanyLookupExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompanyLookupExceptionHandler.class);
    private static final String UNKNOWN_ERROR_CODE = "00000";

    @ExceptionHandler(CompanyLookupException.class)
    protected ResponseEntity<Object> handleBeansException(CompanyLookupException exception, WebRequest request) {
        HttpStatus httpStatus = extractHttpStatus(exception);
        logException(exception, request, httpStatus);
        return handleExceptionInternal(exception, null, new HttpHeaders(), httpStatus, request);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {

        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(CompanyLookupError.create(statusCode.value(), extractErrorCode(ex), ex.getMessage()));
    }

    private String extractErrorCode(Exception ex) {
        if (ex instanceof CompanyLookupException) {
            return ((CompanyLookupException) ex).getErrorCode();
        }
        return UNKNOWN_ERROR_CODE;
    }

    private HttpStatus extractHttpStatus(CompanyLookupException ex) {
        try {
            return HttpStatus.valueOf(ex.getHttpStatus());
        } catch (Exception e) {
            logger.warn("Failed to extract status from exception '{}', status was '{}'", ex, ex.getHttpStatus());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private void logException(Exception exception, WebRequest request, HttpStatus responseStatus) {

        String logMessage = getLogMessage(exception, (ServletWebRequest) request, responseStatus);

        switch (responseStatus) {
            case BAD_REQUEST, NOT_FOUND, CONFLICT:
                logger.info(logMessage);
                break; // 400, 404, 409
            case UNAUTHORIZED, FORBIDDEN:
                logger.warn(logMessage);
                break; // 401, 403
            case INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE:
            default:
                logger.error(logMessage, exception);
                break; // 500 and unknown status
        }
    }

    private String getLogMessage(Exception exception, ServletWebRequest request, HttpStatus responseStatus) {
        String path = request.getRequest().getRequestURI();
        if (path == null || path.isBlank()) {
            path = "no-path";
        }

        return String.format(
                "Request:  method: (%s), path: (%s). Response: http status (%s), error code (%s), message: (%s)",
                request.getHttpMethod(),
                path,
                responseStatus,
                extractErrorCode(exception),
                exception.getMessage()
        );
    }
}