package com.smartloansuite.user_service.service;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "Verify your SmartLoanSuite account";
        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?code=" + verificationCode;

        String body = """
                <h2>Welcome to SmartLoanSuite 🎉</h2>
                <p>Click the link below to verify your email address:</p>
                <a href="%s">Verify Email</a>
                """.formatted(verificationUrl);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            mimeMessage.setFrom("no-reply@smartloansuite.com");
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(body, "text/html");
        };

        mailSender.send(messagePreparator);
    }
}

