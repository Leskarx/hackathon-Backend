package com.example.hackathon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hackathon.dto.CategoryDto;
import com.example.hackathon.rules.CategorizationRule;
import com.example.hackathon.service.DuplicateScannerService;

// Add these new endpoints to your existing FileCleanerController

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private DuplicateScannerService scannerService;

    @PostMapping("/rules")
public ResponseEntity<?> addOrUpdateRule(@RequestBody CategorizationRule rule) {
    // Add validation
    if (rule.getMatch() == null || rule.getMatch().isBlank() || 
        rule.getCategory() == null || rule.getCategory().isBlank()) {
        return ResponseEntity.badRequest().body("Match and category fields cannot be null or empty");
    }
    
    try {
        scannerService.addOrUpdateRule(rule);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Failed to update rules: " + e.getMessage());
    }
}

    @GetMapping("/rules")
    public ResponseEntity<List<CategorizationRule>> getAllRules() {
        return ResponseEntity.ok(scannerService.getAllRules());
    }

    @DeleteMapping("/rules/{match}")
    public ResponseEntity<?> deleteRule(@PathVariable String match) {
        try {
            scannerService.deleteRule(match);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete rule: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<CategoryDto>> getCategorizedFiles(
            @RequestParam String directoryPath,
            @RequestParam(required = false, defaultValue = "false") boolean recursive) {
        try {
            List<CategoryDto> categories = scannerService.getCategorizedFiles(directoryPath, recursive);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}