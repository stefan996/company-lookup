package com.company.company_lookup.service.client.serialization;

import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FreeServiceCompanySerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests that a {@link FreeServiceCompany} object is correctly serialized into JSON format.
     */
    @Test
    void shouldSerializeFreeServiceCompany() throws JsonProcessingException {
        // Arrange
        FreeServiceCompany company = new FreeServiceCompany(
                "company-id",
                "company-name",
                LocalDate.of(2025, 3, 31),
                "company-address",
                true
        );

        // Act
        String jsonResult = objectMapper.writeValueAsString(company);

        // Assert
        assertTrue(jsonResult.contains("\"cin\":\"company-id\""));
        assertTrue(jsonResult.contains("\"name\":\"company-name\""));
        assertTrue(jsonResult.contains("\"registration_date\":\"2025-03-31\""));
        assertTrue(jsonResult.contains("\"address\":\"company-address\""));
        assertTrue(jsonResult.contains("\"is_active\":true"));
    }

    /**
     * Tests that a JSON string is correctly deserialized into a {@link FreeServiceCompany} object.
     */
    @Test
    void shouldDeserializeFreeServiceCompany() throws JsonProcessingException {
        // Arrange
        String json = "{\"cin\":\"company-id\"," +
                "\"name\":\"company-name\"," +
                "\"registration_date\":\"2025-03-31\"," +
                "\"address\":\"company-address\"," +
                "\"is_active\":true}";

        // Act
        FreeServiceCompany company = objectMapper.readValue(json, FreeServiceCompany.class);

        // Assert
        assertNotNull(company);
        assertEquals("company-id", company.getCin());
        assertEquals("company-name", company.getName());
        assertEquals(LocalDate.of(2025, 3, 31), company.getRegistrationDate());
        assertEquals("company-address", company.getAddress());
        assertTrue(company.isActive());
    }
}
