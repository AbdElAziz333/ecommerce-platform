package com.aziz.gateway.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String firstName;

    @Column(nullable = false, length = 30)
    private String lastName;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    // ROLE_USER, ROLE_VENDOR, ROLE_ADMIN
    @Column(nullable = false, length = 20)
    private String role;

    // ARABIC, ENGLISH, RUSSIAN, FRENCH
    @Column(nullable = false, length = 20)
    private String preferredLanguage;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}