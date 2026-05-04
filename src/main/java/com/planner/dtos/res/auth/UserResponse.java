package com.planner.dtos.res.auth;

import com.planner.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Long dateOfBirth;
    private Gender gender;
    private String occupation;
    private String profileImageUrl;
    private Boolean isEmailVerified;
    private Boolean isOnboardingComplete;
    private Boolean isGuest;
}
