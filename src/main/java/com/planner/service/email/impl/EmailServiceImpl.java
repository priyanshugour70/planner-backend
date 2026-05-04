package com.planner.service.email.impl;

import com.planner.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@planner.app}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Async
    @Override
    public void sendVerificationEmail(String toEmail, String userName, String verificationToken) {
        String subject = "Verify your email - Planner";
        String verifyUrl = frontendUrl + "/verify-email?token=" + verificationToken;

        String body = buildHtmlTemplate(
                "Verify Your Email",
                "Hi " + userName + ",",
                "Thank you for signing up for Planner! Please verify your email address by clicking the button below.",
                verifyUrl,
                "Verify Email",
                "If you didn't create an account, you can safely ignore this email."
        );

        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        String subject = "Reset your password - Planner";
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        String body = buildHtmlTemplate(
                "Reset Your Password",
                "Hi " + userName + ",",
                "We received a request to reset your password. Click the button below to set a new password.",
                resetUrl,
                "Reset Password",
                "If you didn't request a password reset, you can safely ignore this email. This link will expire in 1 hour."
        );

        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendPasswordChangedNotification(String toEmail, String userName) {
        String subject = "Password changed - Planner";

        String body = buildHtmlTemplate(
                "Password Changed",
                "Hi " + userName + ",",
                "Your password has been successfully changed. If you did not make this change, please contact support immediately.",
                null,
                null,
                "For security, all your active sessions have been signed out."
        );

        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    @Override
    public void sendWelcomeEmail(String toEmail, String userName) {
        String subject = "Welcome to Planner!";

        String body = buildHtmlTemplate(
                "Welcome to Planner!",
                "Hi " + userName + ",",
                "We're excited to have you on board. Planner helps you organize your goals, tasks, habits, and more — all in one place.",
                frontendUrl + "/dashboard",
                "Get Started",
                "If you have any questions, don't hesitate to reach out to our support team."
        );

        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent successfully to {} with subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {} with subject: {}. Error: {}", to, subject, e.getMessage());
        }
    }

    private String buildHtmlTemplate(String title, String greeting, String message,
                                     String actionUrl, String actionText, String footer) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='")
            .append("font-family:-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,sans-serif;")
            .append("margin:0;padding:0;background-color:#f5f5f5;'>");
        html.append("<div style='max-width:600px;margin:40px auto;background:#ffffff;border-radius:12px;")
            .append("box-shadow:0 2px 8px rgba(0,0,0,0.08);overflow:hidden;'>");

        html.append("<div style='background:#4F46E5;padding:32px;text-align:center;'>");
        html.append("<h1 style='color:#ffffff;margin:0;font-size:24px;'>").append(title).append("</h1>");
        html.append("</div>");

        html.append("<div style='padding:32px;'>");
        html.append("<p style='font-size:16px;color:#333;margin-bottom:8px;'>").append(greeting).append("</p>");
        html.append("<p style='font-size:15px;color:#555;line-height:1.6;'>").append(message).append("</p>");

        if (actionUrl != null && actionText != null) {
            html.append("<div style='text-align:center;margin:32px 0;'>");
            html.append("<a href='").append(actionUrl).append("' style='display:inline-block;")
                .append("background:#4F46E5;color:#ffffff;text-decoration:none;padding:14px 32px;")
                .append("border-radius:8px;font-size:16px;font-weight:600;'>")
                .append(actionText).append("</a>");
            html.append("</div>");
        }

        html.append("<p style='font-size:13px;color:#999;margin-top:24px;'>").append(footer).append("</p>");
        html.append("</div>");

        html.append("<div style='background:#f9f9f9;padding:16px;text-align:center;'>");
        html.append("<p style='font-size:12px;color:#aaa;margin:0;'>&copy; Planner. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");
        return html.toString();
    }
}
