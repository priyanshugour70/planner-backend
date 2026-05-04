package com.planner.dtos.req.auth;

import com.planner.enums.Gender;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must be at most 100 characters")
    private String lastName;

    private String phoneNumber;
    private Long dateOfBirth;
    private Gender gender;
    private String occupation;
    private String profileImageUrl;
    private Boolean isOnboardingComplete;
}
