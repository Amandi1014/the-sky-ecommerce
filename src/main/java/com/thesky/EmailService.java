package com.thesky;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("The Sky - Password Reset Request");
        message.setText("You requested a password reset.\n\n"
                + "Click the link below to set a new password:\n"
                + resetLink + "\n\n"
                + "This link will expire in 30 minutes.\n"
                + "If you didn't request this, you can safely ignore this email.");
        mailSender.send(message);
    }
}