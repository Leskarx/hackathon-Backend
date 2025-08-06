package com.example.hackathon.dto;


public class FileDto {
    private String path;
    private long size;
    private String lastModified;

    // Getters and setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
}