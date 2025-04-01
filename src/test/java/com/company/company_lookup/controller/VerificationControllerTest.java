package com.company.company_lookup.controller;

import com.company.company_lookup.AppProfiles;
import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.model.CompanyServiceEnum;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.repository.VerificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(AppProfiles.TEST)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        verificationRepository.deleteAll();
    }

    /**
     * Tests whether a valid Verification object is returned when an existing verification ID is provided.
     */
    @Test
    public void shouldReturnValidVerificationWhenExists() throws Exception {
        // Arrange
        UUID validVerificationId = UUID.randomUUID();
        String queryText = "Example Query";
        Verification verification = new Verification(
                validVerificationId,
                queryText,
                LocalDateTime.now(),
                "Example Result",
                CompanyServiceEnum.FREE
        );
        verificationRepository.save(verification);

        // Act
        MvcResult mvcResult = mockMvc.perform(get("/verifications/" + validVerificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId").value(validVerificationId.toString()))
                .andExpect(jsonPath("$.queryText").value(queryText))
                .andReturn();

        // Assert
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        VerificationResponse verificationResponse = objectMapper.readValue(jsonResponse, VerificationResponse.class);

        assertNotNull(verificationResponse);
        assertEquals(queryText, verificationResponse.getQueryText());
        assertEquals(verification.getVerificationId(), verificationResponse.getVerificationId());
        assertEquals(verification.getResult(), verificationResponse.getResult());
    }

    /**
     * Tests whether a NOT_FOUND error is returned when a non-existent verification ID is provided.
     */
    @Test
    public void shouldReturnNotFoundForNonExistentVerification() throws Exception {
        // Arrange
        String verificationDoesNotExist = UUID.randomUUID().toString();

        // Act and Assert
        mockMvc.perform(get("/verifications/" + verificationDoesNotExist))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests whether a BAD_REQUEST error is returned when an invalid verification ID is provided.
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", "12345", "invalid-uuid"})
    void shouldReturnBadRequestWhenInvalidVerificationIdIsProvided(String verificationId) throws Exception {
        mockMvc.perform(get("/verifications/" + verificationId))
                .andExpect(status().isBadRequest());

    }
}
