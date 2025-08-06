package com.example.hackathon.dto;
import java.util.List;

public class DeleteDuplicatesRequestDto {
    private List<DuplicateGroupDto> duplicateGroups;

    // Getters and setters
    public List<DuplicateGroupDto> getDuplicateGroups() { return duplicateGroups; }
    public void setDuplicateGroups(List<DuplicateGroupDto> duplicateGroups) { this.duplicateGroups = duplicateGroups; }
}