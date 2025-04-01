package com.company.company_lookup.controller;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.company.company_lookup.exception.CompanyLookupError.INTERNAL_SERVER_ERROR;
import static com.company.company_lookup.exception.CompanyLookupError.SERVICE_UNAVAILABLE;

@RestController
public class ThirdPartyController {

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartyController.class);
    private static final Random random = new Random();

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${companies.path.free}")
    private String freeCompanyResource;

    @Value("${companies.path.premium}")
    private String premiumCompanyResource;

    private List<FreeServiceCompany> freeCompanies;
    private List<PremiumServiceCompany> premiumCompanies;

    public ThirdPartyController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        ClassPathResource freeClassPathResource = new ClassPathResource(freeCompanyResource);
        ClassPathResource premiumClassPathResource = new ClassPathResource(premiumCompanyResource);

        try {
            freeCompanies = objectMapper.readValue(freeClassPathResource.getInputStream(), new TypeReference<>() {});
            premiumCompanies = objectMapper.readValue(premiumClassPathResource.getInputStream(), new TypeReference<>() {});
        } catch (IOException e) {
            logger.error("Failed to load company data from JSON files. Error: {}", e.getMessage(), e);
            throw new CompanyLookupException(
                    INTERNAL_SERVER_ERROR.withMessage("Failed to load company data from JSON files")
            );
        }
    }

    /**
     * Retrieves a list of free service companies that match the given query.
     * <p>
     * The query is used to filter companies based on their company identification number (CIN).
     * There is a 40% chance that the service will be unavailable, in which case an exception is thrown.
     *
     * @param query The search query used to filter companies by CIN.
     * @return A list of {@link FreeServiceCompany} objects that match the query.
     * @throws CompanyLookupException If the service is unavailable.
     */
    @GetMapping("/free-third-party")
    public ResponseEntity<List<FreeServiceCompany>> getFreeCompanies(@RequestParam String query) {
        if (random.nextInt(10) < 4) {
            logger.warn("Free service is currently unavailable. Query: '{}'", query);
            throw new CompanyLookupException(
                    SERVICE_UNAVAILABLE.withMessage("Free service is currently unavailable")
            );
        }

        List<FreeServiceCompany> filteredCompanies = freeCompanies.stream()
                .filter(company -> company.getCin().contains(query))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredCompanies);
    }

    /**
     * Retrieves a list of premium service companies that match the given query.
     * <p>
     * The query is used to filter companies based on their company identification number.
     * There is a 10% chance that the service will be unavailable, in which case an exception is thrown.
     *
     * @param query The search query used to filter companies by company identification number.
     * @return A list of {@link PremiumServiceCompany} objects that match the query.
     * @throws CompanyLookupException If the service is unavailable.
     */
    @GetMapping("/premium-third-party")
    public ResponseEntity<List<PremiumServiceCompany>> getPremiumCompanies(@RequestParam String query) {
        if (random.nextInt(10) < 1) {
            logger.warn("Premium service is currently unavailable. Query: '{}'", query);
            throw new CompanyLookupException(
                    SERVICE_UNAVAILABLE.withMessage("Premium service is currently unavailable")
            );
        }

        List<PremiumServiceCompany> filteredCompanies = premiumCompanies.stream()
                .filter(company -> company.getCompanyIdentificationNumber().contains(query))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredCompanies);
    }
}