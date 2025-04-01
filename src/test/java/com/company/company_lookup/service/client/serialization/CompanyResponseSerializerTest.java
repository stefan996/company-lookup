package com.company.company_lookup.service.client.serialization;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.serializer.CompanyResultSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompanyResponseSerializerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyResultSerializer serializer;

    /**
     * Tests serialization of a valid list of CompanyResult objects.
     */
    @Test
    void shouldSerializeCompanyResultsSuccessfully() {
        // Arrange
        List<CompanyResponse> companies = List.of(
                new CompanyResponse(
                        "1",
                        "company-1",
                        "2025-01-01",
                        "company-address-2",
                        true
                ),
                new CompanyResponse(
                        "2",
                        "company-2",
                        "2025-03-31",
                        "company-address-2",
                        true
                )
        );

        // Act
        String result = serializer.serializeCompanyResults(companies);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("company-1"));
        assertTrue(result.contains("company-2"));
    }

    /**
     * Tests serialization of an empty list, expecting the default NO_MATCHES_FOUND_VALUE.
     */
    @Test
    void shouldReturnNoMatchesFoundForEmptyList() {
        // Act
        String result = serializer.serializeCompanyResults(List.of());

        // Assert
        assertEquals(CompanyResultSerializer.NO_MATCHES_FOUND_VALUE, result);
    }

    /**
     * Tests serialization failure handling by throwing CompanyLookupException.
     */
    @Test
    void shouldThrowExceptionOnSerializationFailure() {
        // Arrange
        ObjectMapper faultyObjectMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("Serialization error") {
                };
            }
        };
        CompanyResultSerializer faultySerializer = new CompanyResultSerializer(faultyObjectMapper);

        List<CompanyResponse> companies = List.of(
                new CompanyResponse(
                        "1",
                        "company-1",
                        "2025-01-01",
                        "company-address",
                        true
                )
        );

        // Act and Assert
        CompanyLookupException exception = assertThrows(CompanyLookupException.class, () ->
                faultySerializer.serializeCompanyResults(companies));
        assertEquals("Failed to serialize company list", exception.getMessage());
    }

    /**
     * Tests handling of a deserialization failure, expecting the original string as output.
     */
    @Test
    void shouldReturnOriginalStringOnDeserializationFailure() {
        // Arrange
        String invalidJson = "invalid-json";

        // Act
        Object result = serializer.deSerializeCompanyResults(invalidJson);

        // Assert
        assertEquals(invalidJson, result);
    }
}
