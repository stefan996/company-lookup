package com.company.company_lookup.service.client.model;

import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.model.CompanyServiceEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ThirdPartyResponse {
    private List<CompanyResponse> companyList;
    private CompanyServiceEnum companyServiceEnum;
}
