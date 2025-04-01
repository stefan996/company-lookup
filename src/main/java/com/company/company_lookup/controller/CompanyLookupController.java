package com.company.company_lookup.controller;

import com.company.company_lookup.model.CompanyLookupResponse;
import com.company.company_lookup.service.CompanyLookupService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend-service")
public class CompanyLookupController {

    @Autowired
    private CompanyLookupService companyLookupService;

    /**
     * Main backend endpoint for searching companies based on the provided query.
     * <p>
     * This method allows users to search for companies by providing a query term.
     * It returns a {@link CompanyLookupResponse} with the details of matching companies.
     *
     * @param query          the search query used to find companies
     * @param verificationId a unique identifier to track the search request
     * @return a {@link CompanyLookupResponse} containing the company information if a match is found
     */
    @Operation(summary = "Lookup companies", description = "Search companies based on the provided query")
    @GetMapping
    public CompanyLookupResponse lookupCompanies(@RequestParam String query, @RequestParam String verificationId) {
        return companyLookupService.lookupCompanies(query, verificationId);
    }
}
