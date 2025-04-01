package com.company.company_lookup.service.validator;

import com.company.company_lookup.exception.CompanyLookupException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RequestParamValidatorTest {

    @Autowired
    private RequestParamValidator requestParamValidator;

    /**
     * Test verifies that null or empty parameters throw a CompanyLookupException with a BAD_REQUEST error.
     */
    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowExceptionForNullOrEmptyParameters(String parameter) {
        assertThrows(CompanyLookupException.class, () ->
                requestParamValidator.validate(parameter, "test-parameter"));
    }

    /**
     * Test verifies that valid parameters do not throw any exception when passed to the validate method.
     */
    @ParameterizedTest
    @ValueSource(strings = {"valid-value", "12345", "some-text"})
    void shouldNotThrowExceptionForValidParameters(String parameter) {
        assertDoesNotThrow(() ->
                requestParamValidator.validate(parameter, "test-parameter"));
    }

    /**
     * Test verifies that invalid UUID values throw a CompanyLookupException with a BAD_REQUEST error
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid-uuid", "12345"})
    void shouldThrowExceptionForInvalidUUIDs(String parameter) {
        assertThrows(CompanyLookupException.class, () ->
                requestParamValidator.validateUUID(parameter, "test-parameter"));
    }

    /**
     * Test verifies that valid UUID values do not throw any exception
     */
    @ParameterizedTest
    @ValueSource(strings = {"550e8400-e29b-41d4-a716-446655440000", "550e8400-e29b-41d4-a716-446655440000"})
    void shouldNotThrowExceptionForValidUUIDs(String parameter) {
        assertDoesNotThrow(() ->
                requestParamValidator.validateUUID(parameter, "test-parameter"));
    }
}