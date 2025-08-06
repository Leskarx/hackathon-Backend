package com.example.hackathon.dto;

import java.util.List;

import com.example.hackathon.rules.CategorizationRule;

public class ConfigDto {
    private List<String> scanPaths;
    private List<CategorizationRule> rules;

    // Getters and setters
    public List<String> getScanPaths() { return scanPaths; }
    public void setScanPaths(List<String> scanPaths) { this.scanPaths = scanPaths; }
    public List<CategorizationRule> getRules() { return rules; }
    public void setRules(List<CategorizationRule> rules) { this.rules = rules; }
}