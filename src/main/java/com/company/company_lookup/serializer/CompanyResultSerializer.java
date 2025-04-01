package com.company.company_lookup.serializer;

import com.company.company_lookup.exception.CompanyLookupException;
import com.company.company_lookup.model.CompanyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.company.company_lookup.exception.CompanyLookupError.INTERNAL_SERVER_ERROR;

@Component
public class CompanyResultSerializer {

    public static final String NO_MATCHES_FOUND_VALUE = "No matches found";
    private static final Logger logger = LoggerFactory.getLogger(CompanyResultSerializer.class);

    @Autowired
    private ObjectMapper objectMapper;

    public CompanyResultSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serializeCompanyResults(List<CompanyResponse> companies) {
        if (companies == null || companies.isEmpty()) {
            return NO_MATCHES_FOUND_VALUE;
        }

        try {
            return objectMapper.writeValueAsString(companies);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize company list", e);
            throw new CompanyLookupException(INTERNAL_SERVER_ERROR.withMessage("Failed to serialize company list"));
        }
    }

    public Object deSerializeCompanyResults(String result) {
        try {
            return objectMapper.readValue(result, List.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize company results: {}", result);
            return result;
        }
    }
}
