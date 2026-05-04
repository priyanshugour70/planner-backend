package com.planner.entities.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.planner.entities.BaseEntity;
import com.planner.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private Long dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Column(name = "is_onboarding_complete")
    private Boolean isOnboardingComplete = false;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "fcm_token")
    private String fcmToken;

    public String getFullName() {
        return (firstName + " " + (lastName != null ? lastName : "")).trim();
    }
}
