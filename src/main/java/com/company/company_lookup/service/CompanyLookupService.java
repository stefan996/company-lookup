package com.company.company_lookup.service;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyLookupResponse;
import com.company.company_lookup.model.CompanyResponse;
import com.company.company_lookup.service.client.model.ThirdPartyResponse;
import com.company.company_lookup.serializer.CompanyResultSerializer;
import com.company.company_lookup.service.client.ThirdPartyLookupHandler;
import com.company.company_lookup.service.validator.RequestParamValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.company.company_lookup.model.CompanyServiceEnum.PREMIUM;

@Service
public class CompanyLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyLookupService.class);
    public static final String ERROR_BOTH_ENDPOINTS_DOWN =
            "Both free and premium company endpoints are unable to process request";

    private final ThirdPartyLookupHandler thirdPartyLookupHandler;
    private final CompanyResultSerializer companyResultSerializer;
    private final VerificationService verificationService;
    private final RequestParamValidator requestParamValidator;

    public CompanyLookupService(ThirdPartyLookupHandler thirdPartyLookupHandler,
                                CompanyResultSerializer companyResultSerializer,
                                VerificationService verificationService,
                                RequestParamValidator requestParamValidator) {

        this.thirdPartyLookupHandler = thirdPartyLookupHandler;
        this.companyResultSerializer = companyResultSerializer;
        this.verificationService = verificationService;
        this.requestParamValidator = requestParamValidator;
    }

    public CompanyLookupResponse lookupCompanies(String query, String verificationId) {
        logger.info("Looking up companies for query: '{}' and verification id '{}'", query, verificationId);

        requestParamValidator.validate(query, "query");
        requestParamValidator.validateUUID(verificationId, "verificationId");

        try {
            ThirdPartyResponse thirdPartyResponse = thirdPartyLookupHandler.fetchCompanies(query);
            List<CompanyResponse> companyResponseList = thirdPartyResponse.getCompanyList();
            verificationService.saveVerification(
                    UUID.fromString(verificationId),
                    query,
                    companyResultSerializer.serializeCompanyResults(companyResponseList),
                    thirdPartyResponse.getCompanyServiceEnum()
            );

            return createCompanyLookupResponse(UUID.fromString(verificationId), query, companyResponseList);
        } catch (CompanyLookupException e) {
            logger.error("Both free and premium company endpoints are unable to process request " +
                    "for query '{}' and verification id '{}'", query, verificationId);
            verificationService.saveVerification(
                    UUID.fromString(verificationId),
                    query,
                    ERROR_BOTH_ENDPOINTS_DOWN,
                    PREMIUM
            );
            throw e;
        }
    }

    private CompanyLookupResponse createCompanyLookupResponse(UUID verificationId,
                                                              String query,
                                                              List<CompanyResponse> companies) {

        CompanyResponse firstResult = companies.isEmpty() ? null : companies.getFirst();

        List<CompanyResponse> otherResults = companies.size() > 1 ?
                companies.subList(1, companies.size()) :
                Collections.emptyList();

        return new CompanyLookupResponse(verificationId, query, firstResult, otherResults);
    }
}
