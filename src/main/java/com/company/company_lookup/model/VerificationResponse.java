package com.company.company_lookup.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
    private UUID verificationId;
    private String queryText;
    private Object result;
}
