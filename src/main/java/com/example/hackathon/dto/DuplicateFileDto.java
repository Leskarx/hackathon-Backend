package com.example.hackathon.dto;

public class DuplicateFileDto {
    private String path;
    private long size;
    private String lastModified;
    private boolean selected; // For frontend to mark which to keep

    // Getters and setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public String getLastModified() { return lastModified; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
