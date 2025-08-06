package com.example.hackathon.dto;

import java.util.List;

public class ScanResponseDto {
    private String message;
    private boolean success;
    private String logFilePath;
    private List<DuplicateGroupDto> duplicateGroups;

    // Getters and setters
    public List<DuplicateGroupDto> getDuplicateGroups() { return duplicateGroups; }
    public void setDuplicateGroups(List<DuplicateGroupDto> duplicateGroups) { 
        this.duplicateGroups = duplicateGroups; 
    }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getLogFilePath() { return logFilePath; }
    public void setLogFilePath(String logFilePath) { this.logFilePath = logFilePath; }
}