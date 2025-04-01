package com.company.company_lookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CompanyLookupApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyLookupApplication.class, args);
    }

}
