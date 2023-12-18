package com.socialmedia.socialmedia.services.impl;

import com.socialmedia.socialmedia.entities.VerificationToken;
import com.socialmedia.socialmedia.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void sendVerificationToken(VerificationToken token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(token.getUser().getEmail());
        message.setSubject("NoReply: Verify your email");
        message.setText("Thank you for registering: "+ token.getUser().getDisplayName() + "\n\nUse the following 6-digit code to verify your email: \n\n" + token.getToken());
        javaMailSender.send(message);
    }

    @Override
    public void sendEmailWithEmbeddedImage(String to, String subject, String body, String image) {

    }

    @Override
    public void sendEmailWithEmbeddedFile(String to, String subject, String body, String file) {

    }

    @Override
    public void sendHtmlEmail(String to, String subject, String body) {

    }

    @Override
    public void sendHtmlEmailWithEmbeddedFile(String to, String subject, String body, String file) {

    }
}
