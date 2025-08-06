package com.example.hackathon.dto;

public class EmailRequestDto {
    private String email;
    private boolean sendEmail;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isSendEmail() { return sendEmail; }
    public void setSendEmail(boolean sendEmail) { this.sendEmail = sendEmail; }
}
