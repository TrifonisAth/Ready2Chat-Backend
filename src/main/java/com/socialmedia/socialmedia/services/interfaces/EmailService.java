package com.socialmedia.socialmedia.services.interfaces;

import com.socialmedia.socialmedia.entities.VerificationToken;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendVerificationToken(VerificationToken token);
    void sendEmailWithEmbeddedImage(String to, String subject, String body, String image);
    void sendEmailWithEmbeddedFile(String to, String subject, String body, String file);
    void sendHtmlEmail(String to, String subject, String body);
    void sendHtmlEmailWithEmbeddedFile(String to, String subject, String body, String file);
}
