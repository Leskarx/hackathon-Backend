package com.example.hackathon.dto;

import java.util.List;

public class CategoryDto {
    private String name;
    private List<FileDto> files;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<FileDto> getFiles() { return files; }
    public void setFiles(List<FileDto> files) { this.files = files; }
}