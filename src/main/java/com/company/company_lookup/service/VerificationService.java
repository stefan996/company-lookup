package com.company.company_lookup.service;

import com.company.company_lookup.converter.VerificationConverter;
import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyServiceEnum;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.repository.VerificationRepository;
import com.company.company_lookup.service.validator.RequestParamValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.company.company_lookup.exception.CompanyLookupError.NOT_FOUND;
import static com.company.company_lookup.service.config.CacheConfig.VERIFICATION_CACHE_NAME;

@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private final VerificationRepository verificationRepository;
    private final RequestParamValidator requestParamValidator;
    private final VerificationConverter verificationConverter;

    public VerificationService(VerificationRepository verificationRepository,
                               RequestParamValidator requestParamValidator,
                               VerificationConverter verificationConverter) {

        this.verificationRepository = verificationRepository;
        this.requestParamValidator = requestParamValidator;
        this.verificationConverter = verificationConverter;
    }

    @Cacheable(value = VERIFICATION_CACHE_NAME, key = "#verificationId")
    public VerificationResponse getVerificationById(String verificationId) {
        logger.info("Get verification by ID '{}'", verificationId);
        requestParamValidator.validateUUID(verificationId, "verificationId");

        Verification verification = verificationRepository.findById(UUID.fromString(verificationId))
                .orElseThrow(() -> new CompanyLookupException(
                        NOT_FOUND.withMessage("Verification not found for the provided ID")
                ));
        return verificationConverter.convert(verification);
    }

    public void saveVerification(UUID verificationId,
                                 String query,
                                 String result,
                                 CompanyServiceEnum companyServiceEnum) {

        logger.info("Save verification with id '{}', query '{}', result '{}' and company service enum '{}'",
                verificationId, query, result, companyServiceEnum);
        requestParamValidator.validate(query, "query");
        requestParamValidator.validate(result, "result");
        requestParamValidator.validateNotNull(companyServiceEnum, "companyServiceEnum");

        Verification verification = new Verification(
                verificationId,
                query,
                LocalDateTime.now(),
                result,
                companyServiceEnum
        );
        verificationRepository.save(verification);
    }
}

