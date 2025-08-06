package com.example.hackathon.dto;

import java.time.LocalDateTime;

public class LogEntryDto {
    private LocalDateTime timestamp;
    private String action;
    private String details;

    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
