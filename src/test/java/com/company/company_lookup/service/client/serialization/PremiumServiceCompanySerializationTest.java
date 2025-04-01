package com.company.company_lookup.service.client.serialization;

import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PremiumServiceCompanySerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Tests the serialization of a PremiumServiceCompany object to JSON.
     */
    @Test
    void shouldSerializePremiumServiceCompany() throws JsonProcessingException {
        // Arrange
        PremiumServiceCompany company = new PremiumServiceCompany(
                "company-id",
                "company-name",
                LocalDate.of(2025, 3, 31),
                "company-address",
                true
        );

        // Act
        String jsonResult = objectMapper.writeValueAsString(company);

        // Assert
        assertTrue(jsonResult.contains("\"companyIdentificationNumber\":\"company-id\""));
        assertTrue(jsonResult.contains("\"companyName\":\"company-name\""));
        assertTrue(jsonResult.contains("\"registrationDate\":\"2025-03-31\""));
        assertTrue(jsonResult.contains("\"fullAddress\":\"company-address\""));
        assertTrue(jsonResult.contains("\"active\":true"));
    }

    /**
     * Tests the deserialization of a JSON string into a PremiumServiceCompany object.
     */
    @Test
    void shouldDeserializePremiumServiceCompany() throws JsonProcessingException {
        // Arrange
        String json = "{" +
                "\"companyIdentificationNumber\":\"company-id\"," +
                "\"companyName\":\"company-name\"," +
                "\"registrationDate\":\"2025-03-31\"," +
                "\"fullAddress\":\"company-address\"," +
                "\"active\":true" +
                "}";

        // Act
        PremiumServiceCompany company = objectMapper.readValue(json, PremiumServiceCompany.class);

        // Assert
        assertEquals("company-id", company.getCompanyIdentificationNumber());
        assertEquals("company-name", company.getCompanyName());
        assertEquals(LocalDate.of(2025, 3, 31), company.getRegistrationDate());
        assertEquals("company-address", company.getFullAddress());
        assertTrue(company.isActive());
    }
}
