package com.company.company_lookup.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * PostgreSQL container used for testing.
 * This provides an isolated environment for tests using Testcontainers.
 */
@TestConfiguration
public class PostgreSQLTestContainerConfig {

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:17-alpine")
                    .withDatabaseName("company_lookup")
                    .withUsername("test_user")
                    .withPassword("test_password");

    static {
        POSTGRESQL_CONTAINER.start();
    }

    @Bean
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return POSTGRESQL_CONTAINER;
    }
}

