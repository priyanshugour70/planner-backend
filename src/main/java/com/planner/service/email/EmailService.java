package com.planner.service.email;

public interface EmailService {

    void sendVerificationEmail(String toEmail, String userName, String verificationToken);

    void sendPasswordResetEmail(String toEmail, String userName, String resetToken);

    void sendPasswordChangedNotification(String toEmail, String userName);

    void sendWelcomeEmail(String toEmail, String userName);

    void sendOtpEmail(String toEmail, String otpCode);
}
