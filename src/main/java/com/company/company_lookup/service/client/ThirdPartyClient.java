package com.company.company_lookup.service.client;

import com.company.company_lookup.converter.CompanyConverter;
import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.company.company_lookup.exception.CompanyLookupError.SERVICE_UNAVAILABLE;

@Component
public class ThirdPartyClient {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyClient.class);
    private static final String FREE_SERVICE_URL = "http://localhost:8080/free-third-party?query=";
    private static final String PREMIUM_SERVICE_URL = "http://localhost:8080/premium-third-party?query=";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CompanyConverter companyConverter;

    public List<CompanyResponse> fetchFreeCompanies(String query) {
        try {
            ResponseEntity<List<FreeServiceCompany>> response = restTemplate.exchange(
                    FREE_SERVICE_URL + query,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return companyConverter.convertFromFreeService(response.getBody());
        } catch (HttpServerErrorException e) {
            logger.info("Free service is unavailable '{}'", e.getMessage());
            throw new CompanyLookupException(SERVICE_UNAVAILABLE.withMessage("Free service is unavailable"));
        }
    }

    public List<CompanyResponse> fetchPremiumCompanies(String query) {
        try {
            ResponseEntity<List<PremiumServiceCompany>> response = restTemplate.exchange(
                    PREMIUM_SERVICE_URL + query,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return companyConverter.convertFromPremiumService(response.getBody());
        } catch (HttpServerErrorException e) {
            logger.info("Premium service is unavailable '{}'", e.getMessage());
            throw new CompanyLookupException(SERVICE_UNAVAILABLE.withMessage("Premium service is unavailable"));
        }
    }
}
