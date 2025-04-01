package com.company.company_lookup.service.client.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumServiceCompany {

    private String companyIdentificationNumber;

    private String companyName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    private String fullAddress;

    private boolean isActive;
}