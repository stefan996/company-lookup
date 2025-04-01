package com.company.company_lookup.controller;

import com.company.company_lookup.AppProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(AppProfiles.TEST)
@AutoConfigureMockMvc
class CompanyLookupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test verifies that the API returns a bad request when required parameters are missing.
     */
    @Test
    void shouldReturnBadRequestWhenRequiredParamsAreMissing() throws Exception {
        mockMvc.perform(get("/backend-service")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests whether a BAD_REQUEST error is returned when an invalid query is provided.
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  "})
    void shouldReturnBadRequestForInvalidQuery(String query) throws Exception {
        mockMvc.perform(get("/backend-service")
                        .param("query", query)
                        .param("verificationId", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests whether a BAD_REQUEST error is returned when an invalid verificationId is provided.
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "12345", "invalid-uuid"})
    void shouldReturnBadRequestForInvalidVerificationId(String verificationId) throws Exception {
        mockMvc.perform(get("/backend-service")
                        .param("query", "valid-query")
                        .param("verificationId", verificationId))
                .andExpect(status().isBadRequest());
    }
}
