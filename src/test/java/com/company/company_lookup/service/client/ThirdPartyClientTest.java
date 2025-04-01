package com.company.company_lookup.service.client;

import com.company.company_lookup.converter.CompanyConverter;
import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ThirdPartyClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CompanyConverter companyConverter;

    @InjectMocks
    private ThirdPartyClient thirdPartyClient;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    /**
     * Tests that the fetchFreeCompanies method successfully retrieves and converts
     * company data from the free service.
     */
    @Test
    void fetchFreeCompaniesReturnsResultsSuccessfully() {
        String query = "test-query";
        List<FreeServiceCompany> freeServiceCompanies = List.of(new FreeServiceCompany());
        List<CompanyResponse> expectedResults = List.of(
                new CompanyResponse(
                        "1",
                        "first",
                        "2025-01-01",
                        "address1",
                        true
                ));

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(freeServiceCompanies));
        when(companyConverter.convertFromFreeService(freeServiceCompanies)).
                thenReturn(expectedResults);

        List<CompanyResponse> results = thirdPartyClient.fetchFreeCompanies(query);

        assertEquals(expectedResults, results);
        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verify(companyConverter, times(1))
                .convertFromFreeService(freeServiceCompanies);
    }

    /**
     * Tests that the fetchFreeCompanies method throws an exception when the free service is unavailable.
     */
    @Test
    void fetchFreeCompaniesThrowsExceptionWhenServiceUnavailable() {
        String query = "test-query";

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpServerErrorException.class);

        assertThrows(CompanyLookupException.class, () -> thirdPartyClient.fetchFreeCompanies(query));

        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verifyNoInteractions(companyConverter);
    }

    /**
     * Tests that the fetchPremiumCompanies method successfully retrieves and converts
     * company data from the premium service.
     */
    @Test
    void fetchPremiumCompaniesReturnsResultsSuccessfully() {
        String query = "test-query";
        List<PremiumServiceCompany> premiumServiceCompanies = List.of(new PremiumServiceCompany());
        List<CompanyResponse> expectedResults = List.of(
                new CompanyResponse(
                        "1",
                        "first",
                        "2025-01-01",
                        "address1",
                        true
                )
        );

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(premiumServiceCompanies));
        when(companyConverter.convertFromPremiumService(premiumServiceCompanies))
                .thenReturn(expectedResults);

        List<CompanyResponse> results = thirdPartyClient.fetchPremiumCompanies(query);

        assertEquals(expectedResults, results);
        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verify(companyConverter, times(1)).
                convertFromPremiumService(premiumServiceCompanies);
    }

    /**
     * Tests that the fetchPremiumCompanies method throws an exception when the premium service is unavailable.
     */
    @Test
    void fetchPremiumCompaniesThrowsExceptionWhenServiceUnavailable() {
        String query = "test-query";

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(HttpServerErrorException.class);

        assertThrows(CompanyLookupException.class, () -> thirdPartyClient.fetchPremiumCompanies(query));

        verify(restTemplate, times(1))
                .exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verifyNoInteractions(companyConverter);
    }
}

