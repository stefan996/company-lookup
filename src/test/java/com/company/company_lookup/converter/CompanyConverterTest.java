package com.company.company_lookup.converter;

import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.FreeServiceCompany;
import com.company.company_lookup.service.client.model.PremiumServiceCompany;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CompanyConverterTest {

    @Autowired
    private CompanyConverter companyConverter;

    /**
     * Tests conversion of a list of FreeServiceCompany objects to a list of CompanyResult objects.
     */
    @Test
    void convertFromFreeServiceListOfFreeServiceCompanies() {
        // Arrange
        FreeServiceCompany firstCompany = new FreeServiceCompany(
                "company-id-1",
                "company-name-1",
                LocalDate.now(),
                "company-address-1",
                true
        );
        FreeServiceCompany secondCompany = new FreeServiceCompany(
                "company-id-2",
                "company-name-2",
                LocalDate.now(),
                "company-address-2",
                true
        );

        List<FreeServiceCompany> freeCompanies = List.of(firstCompany, secondCompany);

        // Act
        List<CompanyResponse> results = companyConverter.convertFromFreeService(freeCompanies);

        // Assert
        assertEquals(2, results.size());

        assertEquals(firstCompany.getCin(), results.getFirst().getCompanyId());
        assertEquals(firstCompany.getName(), results.getFirst().getName());
        assertEquals(firstCompany.getAddress(), results.getFirst().getAddress());
        assertEquals(firstCompany.isActive(), results.getFirst().isActive());

        assertEquals(secondCompany.getCin(), results.get(1).getCompanyId());
        assertEquals(secondCompany.getName(), results.get(1).getName());
        assertEquals(secondCompany.getAddress(), results.get(1).getAddress());
        assertEquals(secondCompany.isActive(), results.get(1).isActive());
    }

    /**
     * Tests conversion of a list of PremiumServiceCompany objects to a list of CompanyResult objects.
     */
    @Test
    void convertFromPremiumServiceListOfPremiumServiceCompanies() {
        // Arrange
        PremiumServiceCompany firstCompany = new PremiumServiceCompany(
                "company-id-1",
                "company-name-1",
                LocalDate.now(),
                "company-address-1",
                true
        );
        PremiumServiceCompany secondCompany = new PremiumServiceCompany(
                "company-id-2",
                "company-name-2",
                LocalDate.now(),
                "company-address-2",
                true
        );

        List<PremiumServiceCompany> premiumCompanies = List.of(firstCompany, secondCompany);

        // Act
        List<CompanyResponse> results = companyConverter.convertFromPremiumService(premiumCompanies);

        // Assert
        assertEquals(2, results.size());

        assertEquals(firstCompany.getCompanyIdentificationNumber(), results.getFirst().getCompanyId());
        assertEquals(firstCompany.getCompanyName(), results.getFirst().getName());
        assertEquals(firstCompany.getFullAddress(), results.getFirst().getAddress());
        assertEquals(firstCompany.isActive(), results.getFirst().isActive());

        assertEquals(secondCompany.getCompanyIdentificationNumber(), results.get(1).getCompanyId());
        assertEquals(secondCompany.getCompanyName(), results.get(1).getName());
        assertEquals(secondCompany.getFullAddress(), results.get(1).getAddress());
        assertEquals(secondCompany.isActive(), results.get(1).isActive());
    }
}
