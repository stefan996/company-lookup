package com.company.company_lookup.exception;

import lombok.Getter;

@Getter
public class CompanyLookupException extends RuntimeException {

    private final int httpStatus;
    private final String errorCode;

    public CompanyLookupException(CompanyLookupError companyLookupError) {
        super(companyLookupError.getMessage());
        this.httpStatus = companyLookupError.getHttpStatus();
        this.errorCode = companyLookupError.getErrorCode();
    }

    public CompanyLookupException(int httpStatus, String message, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public CompanyLookupException(int httpStatus, String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }
}
