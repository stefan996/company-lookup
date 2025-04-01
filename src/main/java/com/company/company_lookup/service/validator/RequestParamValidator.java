package com.company.company_lookup.service.validator;

import com.company.company_lookup.exception.CompanyLookupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.company.company_lookup.exception.CompanyLookupError.BAD_REQUEST;

@Component
public class RequestParamValidator {

    private static final Logger logger = LoggerFactory.getLogger(RequestParamValidator.class);

    public void validateUUID(String parameter, String parameterName) {
        validate(parameter, parameterName);

        try {
            UUID.fromString(parameter);
        } catch (IllegalArgumentException e) {
            logger.error("Provided parameter '{}' is not valid UUID value", parameter);
            throw new CompanyLookupException(
                    BAD_REQUEST.withMessage(String.format("Provided '%s' is not valid UUID value", parameterName))
            );
        }
    }

    public void validate(String parameter, String parameterName) {
        if (parameter == null || parameter.isBlank()) {
            logger.error("Provided parameter '{}' should not be null or empty", parameterName);
            throw new CompanyLookupException(
                    BAD_REQUEST.withMessage(String.format("Provided '%s' should not be null or empty", parameterName))
            );
        }
    }

    public void validateNotNull(Object parameter, String parameterName) {
        if (parameter == null) {
            logger.error("Provided parameter '{}' should not be null or empty", parameterName);
            throw new CompanyLookupException(
                    BAD_REQUEST.withMessage(String.format("Provided '%s' should not be null or empty", parameterName))
            );
        }
    }
}
