package com.company.company_lookup.service.client;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.ThirdPartyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.company.company_lookup.exception.CompanyLookupError.SERVICE_UNAVAILABLE;
import static com.company.company_lookup.model.CompanyServiceEnum.FREE;
import static com.company.company_lookup.model.CompanyServiceEnum.PREMIUM;

/**
 * Handler responsible for fetching company data from external third-party services.
 * Implements a failover mechanism where it first tries the free service, and if it fails,
 * it falls back to the premium service.
 */
@Component
public class ThirdPartyLookupHandler {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyLookupHandler.class);

    @Autowired
    private ThirdPartyClient thirdPartyClient;

    public ThirdPartyResponse fetchCompanies(String query) {
        try {
            List<CompanyResponse> companyResponseList = thirdPartyClient.fetchFreeCompanies(query);
            return new ThirdPartyResponse(companyResponseList, FREE);
        } catch (CompanyLookupException e) {
            logger.warn("Failed to fetch from free service: '{}'", e.getMessage());
            return fetchPremiumServiceCompanies(query);
        }
    }

    private ThirdPartyResponse fetchPremiumServiceCompanies(String query) {
        try {
            List<CompanyResponse> companyResponseList = thirdPartyClient.fetchPremiumCompanies(query);
            return new ThirdPartyResponse(companyResponseList, PREMIUM);
        } catch (CompanyLookupException ex) {
            logger.warn("Failed to fetch from premium service: '{}'", ex.getMessage());
            throw new CompanyLookupException(SERVICE_UNAVAILABLE);
        }
    }
}