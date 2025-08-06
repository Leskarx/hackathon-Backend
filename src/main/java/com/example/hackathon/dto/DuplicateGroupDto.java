package com.example.hackathon.dto;

import java.util.List;

public class DuplicateGroupDto {
    private String hash;
    private List<DuplicateFileDto> files;

    // Getters and setters
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public List<DuplicateFileDto> getFiles() { return files; }
    public void setFiles(List<DuplicateFileDto> files) { this.files = files; }
}