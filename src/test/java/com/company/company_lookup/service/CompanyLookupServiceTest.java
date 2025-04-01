package com.company.company_lookup.service;


import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyLookupResponse;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.ThirdPartyResponse;
import com.company.company_lookup.serializer.CompanyResultSerializer;
import com.company.company_lookup.service.client.ThirdPartyLookupHandler;
import com.company.company_lookup.service.validator.RequestParamValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.company.company_lookup.exception.CompanyLookupError.SERVICE_UNAVAILABLE;
import static com.company.company_lookup.model.CompanyServiceEnum.FREE;
import static com.company.company_lookup.model.CompanyServiceEnum.PREMIUM;
import static com.company.company_lookup.service.CompanyLookupService.ERROR_BOTH_ENDPOINTS_DOWN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyLookupServiceTest {

    @Mock
    private ThirdPartyLookupHandler thirdPartyLookupHandler;

    @Mock
    private CompanyResultSerializer companyResultSerializer;

    @Mock
    private VerificationService verificationService;

    @Mock
    private RequestParamValidator requestParamValidator;

    @InjectMocks
    private CompanyLookupService companyLookupService;

    private static final String VALID_QUERY = "valid-query";
    private static final String VALID_VERIFICATION_ID = UUID.randomUUID().toString();

    private List<CompanyResponse> mockCompanyResults() {
        return List.of(
                new CompanyResponse(
                        "1",
                        "first",
                        "2025-01-01",
                        "address1",
                        true
                ),
                new CompanyResponse(
                        "2",
                        "second",
                        "2025-03-31",
                        "address2",
                        true
                )
        );
    }

    @BeforeEach
    void setUp() {
        companyLookupService = new CompanyLookupService(
                thirdPartyLookupHandler,
                companyResultSerializer,
                verificationService,
                requestParamValidator
        );
    }

    /**
     * Tests successful company lookup when the free service is available.
     */
    @Test
    void shouldReturnCompanyLookupResponseWhenServiceIsAvailable() {
        // Arrange
        List<CompanyResponse> companyResponses = mockCompanyResults();
        ThirdPartyResponse thirdPartyResponse = new ThirdPartyResponse(companyResponses, FREE);
        when(thirdPartyLookupHandler.fetchCompanies(VALID_QUERY)).thenReturn(thirdPartyResponse);
        when(companyResultSerializer.serializeCompanyResults(companyResponses)).thenReturn("serialized-results");

        // Act
        CompanyLookupResponse response = companyLookupService.lookupCompanies(VALID_QUERY, VALID_VERIFICATION_ID);

        // Assert
        assertNotNull(response);
        assertEquals(UUID.fromString(VALID_VERIFICATION_ID), response.getVerificationId());
        assertEquals(VALID_QUERY, response.getQuery());
        assertEquals(companyResponses.getFirst(), response.getResult());
        assertEquals(1, response.getOtherResults().size());

        verify(requestParamValidator)
                .validate(VALID_QUERY, "query");
        verify(requestParamValidator)
                .validateUUID(VALID_VERIFICATION_ID, "verificationId");
        verify(verificationService)
                .saveVerification(UUID.fromString(VALID_VERIFICATION_ID), VALID_QUERY, "serialized-results", FREE);
    }

    /**
     * Tests the scenario when both services fail, resulting in a CompanyLookupException.
     */
    @Test
    void shouldThrowExceptionWhenBothServicesFail() {
        // Arrange
        when(thirdPartyLookupHandler.fetchCompanies(VALID_QUERY))
                .thenThrow(new CompanyLookupException(SERVICE_UNAVAILABLE));

        // Act and Assert
        CompanyLookupException exception = assertThrows(CompanyLookupException.class, () ->
                companyLookupService.lookupCompanies(VALID_QUERY, VALID_VERIFICATION_ID));

        assertEquals(SERVICE_UNAVAILABLE.getMessage(), exception.getMessage());

        verify(verificationService)
                .saveVerification(UUID.fromString(VALID_VERIFICATION_ID), VALID_QUERY, ERROR_BOTH_ENDPOINTS_DOWN, PREMIUM);
    }

    /**
     * Tests if the validate method and saveVerification receive the correct parameters.
     */
    @Test
    void shouldValidateParamsAndSaveVerification() {
        // Arrange
        List<CompanyResponse> companyResponses = mockCompanyResults();
        ThirdPartyResponse thirdPartyResponse = new ThirdPartyResponse(companyResponses, FREE);
        when(thirdPartyLookupHandler.fetchCompanies(VALID_QUERY)).thenReturn(thirdPartyResponse);
        when(companyResultSerializer.serializeCompanyResults(companyResponses)).thenReturn("serialized-results");

        // Act
        companyLookupService.lookupCompanies(VALID_QUERY, VALID_VERIFICATION_ID);

        // Assert
        verify(requestParamValidator).validate(VALID_QUERY, "query");
        verify(requestParamValidator).validateUUID(VALID_VERIFICATION_ID, "verificationId");
        verify(verificationService)
                .saveVerification(UUID.fromString(VALID_VERIFICATION_ID), VALID_QUERY, "serialized-results", FREE);
    }
}
