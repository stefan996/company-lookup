package com.company.company_lookup.service;


import com.company.company_lookup.AppProfiles;
import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyServiceEnum;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.repository.VerificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.company.company_lookup.exception.CompanyLookupError.BAD_REQUEST;
import static com.company.company_lookup.exception.CompanyLookupError.NOT_FOUND;
import static com.company.company_lookup.model.CompanyServiceEnum.FREE;
import static com.company.company_lookup.model.CompanyServiceEnum.PREMIUM;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles(AppProfiles.TEST)
@ContextConfiguration(classes = {PostgreSQLTestContainerConfig.class})
@Testcontainers
@SpringBootTest
class VerificationServiceTest extends PostgreSQLTestContainerConfig {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        verificationRepository.deleteAll();
    }

    /**
     * Tests the retrieval of a verification record containing a FREE service company.
     */
    @Test
    public void shouldReturnValidVerificationWithFreeServiceCompany() throws JsonProcessingException {
        // Arrange
        UUID verificationId = UUID.randomUUID();
        FreeServiceCompany freeServiceCompany = new FreeServiceCompany(
                "id-1",
                "name-1",
                LocalDate.now(),
                "address-1",
                true
        );
        String result = objectMapper.writeValueAsString(List.of(freeServiceCompany));

        Verification verification = new Verification(
                verificationId,
                "query-text",
                LocalDateTime.now(),
                result,
                FREE
        );
        verificationRepository.save(verification);

        // Act
        VerificationResponse verificationResponse = verificationService.getVerificationById(verificationId.toString());

        // Assert
        assertEquals(verification.getVerificationId(), verificationResponse.getVerificationId());
        assertEquals(verification.getQueryText(), verificationResponse.getQueryText());
    }

    /**
     * Tests the retrieval of a verification record containing a PREMIUM service company.
     */
    @Test
    public void shouldReturnValidVerificationWithPremiumServiceCompany() throws JsonProcessingException {
        // Arrange
        UUID verificationId = UUID.randomUUID();
        PremiumServiceCompany premiumServiceCompany = new PremiumServiceCompany(
                "id-1",
                "name-1",
                LocalDate.now(),
                "address-1",
                true
        );
        String result = objectMapper.writeValueAsString(List.of(premiumServiceCompany));

        Verification verification = new Verification(
                verificationId,
                "query-test",
                LocalDateTime.now(),
                result,
                PREMIUM
        );
        verificationRepository.save(verification);

        // Act
        VerificationResponse verificationResponse = verificationService.getVerificationById(verificationId.toString());

        // Assert
        assertEquals(verification.getVerificationId(), verificationResponse.getVerificationId());
        assertEquals(verification.getQueryText(), verificationResponse.getQueryText());
    }

    /**
     * Tests that a verification record can be retrieved from the database and remains available in cache
     * after being deleted from the database.
     */
    @Test
    public void shouldReturnValidVerificationWhenFetchedFromCache() {
        // Arrange
        UUID verificationId = UUID.randomUUID();
        Verification verification = new Verification(
                verificationId,
                "test-query",
                LocalDateTime.now(),
                "test-result",
                PREMIUM
        );
        verificationRepository.save(verification);

        // Act
        VerificationResponse verificationResponse = verificationService.getVerificationById(verificationId.toString());

        // Assert
        assertEquals(verification.getVerificationId(), verificationResponse.getVerificationId());
        assertEquals(verification.getQueryText(), verificationResponse.getQueryText());
        assertEquals(verification.getResult(), verificationResponse.getResult().toString());

        // Clean up and check cache behavior
        verificationRepository.deleteById(verificationId);

        // Act - Check if the Verification object can be fetched from cache after deletion
        VerificationResponse verificationFromCache = verificationService.getVerificationById(verificationId.toString());

        // Assert - Ensure the cached verification matches the original verification
        assertEquals(verification.getVerificationId(), verificationFromCache.getVerificationId());
        assertEquals(verification.getQueryText(), verificationFromCache.getQueryText());
        assertEquals(verification.getResult(), verificationFromCache.getResult().toString());
    }

    /**
     * Tests whether a CompanyLookupException is thrown with a NOT_FOUND error code
     * when attempting to retrieve a Verification by a non-existent ID.
     */
    @Test
    public void shouldReturnNotFoundExceptionWhenVerificationDoesNotExist() {
        // Arrange
        String nonExistentVerification = UUID.randomUUID().toString();

        // Act and Assert
        CompanyLookupException companyLookupException = assertThrows(CompanyLookupException.class, () ->
                verificationService.getVerificationById(nonExistentVerification)
        );
        assertEquals(companyLookupException.getHttpStatus(), NOT_FOUND.getHttpStatus());
        assertEquals(companyLookupException.getErrorCode(), NOT_FOUND.getErrorCode());
    }

    /**
     * Tests whether a CompanyLookupException is thrown with a BAD_REQUEST error code
     * when an invalid UUID is provided while attempting to retrieve a Verification.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "12345", "invalid-uuid"})
    public void shouldThrowBadRequestExceptionWhenUUIDIsInvalid(String verificationId) {
        CompanyLookupException companyLookupException = assertThrows(CompanyLookupException.class, () ->
                verificationService.getVerificationById(verificationId)
        );
        assertEquals(companyLookupException.getHttpStatus(), BAD_REQUEST.getHttpStatus());
        assertEquals(companyLookupException.getErrorCode(), BAD_REQUEST.getErrorCode());
    }

    /**
     * Tests that a verification record is successfully saved in the database
     * and can be retrieved afterward.
     */
    @Test
    public void shouldSaveVerificationSuccessfully() {
        // Arrange
        UUID verificationId = UUID.randomUUID();
        String query = "sample-query";
        String result = "sample-result";
        CompanyServiceEnum companyServiceEnum = CompanyServiceEnum.FREE;

        // Act
        verificationService.saveVerification(verificationId, query, result, companyServiceEnum);

        // Assert
        Optional<Verification> savedVerification = verificationRepository.findById(verificationId);
        assertTrue(savedVerification.isPresent());
        assertEquals(verificationId, savedVerification.get().getVerificationId());
        assertEquals(query, savedVerification.get().getQueryText());
        assertEquals(result, savedVerification.get().getResult());
    }
}