package com.example.hackathon.dto;

public class ScanRequestDto {
    private String directoryPath;
    private boolean recursive;
    private OperationType operation;

    // Getters and setters
    public String getDirectoryPath() { return directoryPath; }
    public void setDirectoryPath(String directoryPath) { this.directoryPath = directoryPath; }
    public boolean isRecursive() { return recursive; }
    public void setRecursive(boolean recursive) { this.recursive = recursive; }
    public OperationType getOperation() { return operation; }
    public void setOperation(OperationType operation) { this.operation = operation; }
}
