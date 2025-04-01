package com.company.company_lookup.converter;

import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.serializer.CompanyResultSerializer;
import org.springframework.stereotype.Component;

/**
 * Converter that transforms a {@link Verification} entity into a {@link VerificationResponse}
 * to be returned to the client.
 */
@Component
public class VerificationConverter {

    private final CompanyResultSerializer companyResultSerializer;

    public VerificationConverter(CompanyResultSerializer companyResultSerializer) {
        this.companyResultSerializer = companyResultSerializer;
    }

    public VerificationResponse convert(Verification verification) {
        return new VerificationResponse(
                verification.getVerificationId(),
                verification.getQueryText(),
                companyResultSerializer.deSerializeCompanyResults(verification.getResult())
        );
    }
}
