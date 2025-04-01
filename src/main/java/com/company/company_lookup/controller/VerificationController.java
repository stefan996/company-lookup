package com.company.company_lookup.controller;

import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verifications")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * Retrieves a verification object by its ID.
     * <p>
     * This endpoint fetches a {@link Verification} object associated with the provided verification ID.
     * If a verification with the given ID is found, it returns the object with a 200 (OK) status.
     * If no verification is found, it returns a 404 (Not Found) status.
     *
     * @param verificationId the ID of the verification to retrieve
     * @return a {@link ResponseEntity} containing the verification data if found, or a 404 response if not found
     */
    @Operation(summary = "Get verification by ID", description = "Retrieves a verification using the provided UUID.")
    @GetMapping("/{verificationId}")
    public VerificationResponse getVerification(@PathVariable String verificationId) {
        return verificationService.getVerificationById(verificationId);
    }
}

