package com.company.company_lookup.entity;

import com.company.company_lookup.model.CompanyServiceEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "verifications")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Verification {

    @Id
    private UUID verificationId;

    @Column(nullable = false)
    private String queryText;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String result;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanyServiceEnum source;
}
