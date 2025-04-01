package com.company.company_lookup.converter;

import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter that transforms {@link FreeServiceCompany} and {@link PremiumServiceCompany}
 * entities into {@link CompanyResponse} objects to be returned to the client.
 */
@Component
public class CompanyConverter {

    public List<CompanyResponse> convertFromFreeService(List<FreeServiceCompany> freeServiceCompanyList) {
        return freeServiceCompanyList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public List<CompanyResponse> convertFromPremiumService(List<PremiumServiceCompany> premiumServiceCompanyList) {
        return premiumServiceCompanyList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private CompanyResponse convert(FreeServiceCompany freeServiceCompany) {
        return new CompanyResponse(
                freeServiceCompany.getCin(),
                freeServiceCompany.getName(),
                freeServiceCompany.getRegistrationDate().toString(),
                freeServiceCompany.getAddress(),
                freeServiceCompany.isActive()
        );
    }

    private CompanyResponse convert(PremiumServiceCompany premiumServiceCompany) {
        return new CompanyResponse(
                premiumServiceCompany.getCompanyIdentificationNumber(),
                premiumServiceCompany.getCompanyName(),
                premiumServiceCompany.getRegistrationDate().toString(),
                premiumServiceCompany.getFullAddress(),
                premiumServiceCompany.isActive()
        );
    }
}
