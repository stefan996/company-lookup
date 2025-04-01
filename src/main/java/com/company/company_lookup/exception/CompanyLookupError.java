package com.company.company_lookup.exception;

import lombok.Getter;

@Getter
public class CompanyLookupError {

    public static final CompanyLookupError BAD_REQUEST =
            CompanyLookupError.create(400, "1000", "Bad request");
    public static final CompanyLookupError NOT_FOUND =
            CompanyLookupError.create(404, "2000", "Not found");
    public static final CompanyLookupError INTERNAL_SERVER_ERROR =
            CompanyLookupError.create(500, "3000", "Internal server error");
    public static final CompanyLookupError SERVICE_UNAVAILABLE =
            CompanyLookupError.create(503, "4000", "Service Unavailable");

    private final int httpStatus;
    private final String errorCode;
    private final String message;

    private CompanyLookupError(int httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    public static CompanyLookupError create(int httpStatus, String errorCode, String message) {
        return new CompanyLookupError(httpStatus, errorCode, message);
    }

    public CompanyLookupError withMessage(String message) {
        return CompanyLookupError.create(getHttpStatus(), getErrorCode(), message);
    }
}
