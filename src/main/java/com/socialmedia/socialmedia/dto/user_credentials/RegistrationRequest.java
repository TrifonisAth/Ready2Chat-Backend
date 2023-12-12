package com.socialmedia.socialmedia.dto.user_credentials;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationRequest {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private String displayName;
    private String email;
    private String password;

    public RegistrationRequest() {}

    public RegistrationRequest(String displayName, String email, String password) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public boolean isValid() {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(this.email);
        return matcher.matches() &&
                this.email.length() >= 5 &&
                this.email.length() <= 40 &&
                this.displayName.length() >= 3 &&
                this.displayName.length() <= 30 &&
                this.password.length() >= 8 &&
                this.password.length() <= 30;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
