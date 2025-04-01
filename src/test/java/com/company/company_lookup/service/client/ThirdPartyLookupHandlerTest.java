package com.company.company_lookup.service.client;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.ThirdPartyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.company.company_lookup.exception.CompanyLookupError.SERVICE_UNAVAILABLE;
import static com.company.company_lookup.model.CompanyServiceEnum.FREE;
import static com.company.company_lookup.model.CompanyServiceEnum.PREMIUM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ThirdPartyLookupHandlerTest {

    @Mock
    private ThirdPartyClient thirdPartyClient;

    @InjectMocks
    private ThirdPartyLookupHandler thirdPartyLookupHandler;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    /**
     * Tests the scenario where fetching companies from the free service is successful.
     * This test ensures that the handler correctly returns the companies with the 'FREE' service type.
     */
    @Test
    void fetchCompaniesReturnsFreeServiceResults() {
        // Arrange
        String query = "test-query";
        List<CompanyResponse> mockResults = List.of(
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
        when(thirdPartyClient.fetchFreeCompanies(query)).thenReturn(mockResults);

        // Act
        ThirdPartyResponse response = thirdPartyLookupHandler.fetchCompanies(query);

        // Assert
        assertNotNull(response);
        assertEquals(FREE, response.getCompanyServiceEnum());
        assertEquals(mockResults, response.getCompanyList());
        verify(thirdPartyClient, times(1)).fetchFreeCompanies(query);
        verifyNoMoreInteractions(thirdPartyClient);
    }

    /**
     * Tests the scenario where the free service fails, and the handler falls back to the premium service.
     * This test ensures that the handler correctly falls back to the premium service when the free service fails.
     */
    @Test
    void fetchCompaniesFallsBackToPremiumService() {
        // Arrange
        String query = "test-query";
        List<CompanyResponse> mockResults = List.of(
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
        when(thirdPartyClient.fetchFreeCompanies(query)).
                thenThrow(new CompanyLookupException(SERVICE_UNAVAILABLE));
        when(thirdPartyClient.fetchPremiumCompanies(query)).thenReturn(mockResults);

        // Act
        ThirdPartyResponse response = thirdPartyLookupHandler.fetchCompanies(query);

        // Assert
        assertNotNull(response);
        assertEquals(PREMIUM, response.getCompanyServiceEnum());
        assertEquals(mockResults, response.getCompanyList());
        verify(thirdPartyClient, times(1)).fetchFreeCompanies(query);
        verify(thirdPartyClient, times(1)).fetchPremiumCompanies(query);
    }

    /**
     * Tests the scenario where both the free and premium services are unavailable.
     * This test ensures that the handler throws SERVICE_UNAVAILABLE exception when both services fail.
     */
    @Test
    void fetchCompaniesThrowsExceptionWhenBothServicesFail() {
        // Arrange
        String query = "test-query";
        when(thirdPartyClient.fetchFreeCompanies(query))
                .thenThrow(new CompanyLookupException(SERVICE_UNAVAILABLE));
        when(thirdPartyClient.fetchPremiumCompanies(query))
                .thenThrow(new CompanyLookupException(SERVICE_UNAVAILABLE));

        // Act and Assert
        CompanyLookupException exception = assertThrows(CompanyLookupException.class, () -> {
            thirdPartyLookupHandler.fetchCompanies(query);
        });

        assertEquals(SERVICE_UNAVAILABLE.getHttpStatus(), exception.getHttpStatus());
        verify(thirdPartyClient, times(1)).fetchFreeCompanies(query);
        verify(thirdPartyClient, times(1)).fetchPremiumCompanies(query);
    }
}
