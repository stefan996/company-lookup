package com.company.company_lookup.converter;

import com.company.company_lookup.entity.Verification;
import com.company.company_lookup.model.VerificationResponse;
import com.company.company_lookup.serializer.CompanyResultSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class VerificationConverterTest {

    @Mock
    private CompanyResultSerializer companyResultSerializer;

    @InjectMocks
    private VerificationConverter verificationConverter;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    /**
     * Tests that the convert method correctly maps a Verification entity
     * to a VerificationResponse DTO when valid input is provided.
     */
    @Test
    void shouldConvertVerificationToVerificationResponse() {
        // Arrange
        Verification verification = new Verification();
        verification.setVerificationId(UUID.randomUUID());
        verification.setQueryText("test-query");
        verification.setResult("serialized-result");

        when(companyResultSerializer.deSerializeCompanyResults("serialized-result"))
                .thenReturn("deserialized-result");

        // Act
        VerificationResponse result = verificationConverter.convert(verification);

        // Assert
        assertNotNull(result);
        assertEquals(verification.getVerificationId(), result.getVerificationId());
        assertEquals(verification.getQueryText(), result.getQueryText());
        assertEquals("deserialized-result", result.getResult());
    }

    /**
     * Tests that the convert method throws a NullPointerException
     * when a null Verification entity is provided.
     */
    @Test
    void shouldThrowExceptionWhenVerificationIsNull() {
        assertThrows(NullPointerException.class, () -> verificationConverter.convert(null));
    }
}

