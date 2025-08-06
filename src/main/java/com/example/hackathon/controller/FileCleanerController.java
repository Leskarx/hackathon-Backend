// src/main/java/com/example/hackathon/controller/FileCleanerController.java
package com.example.hackathon.controller;

import com.example.hackathon.dto.*;
import com.example.hackathon.service.DuplicateScannerService;
import com.example.hackathon.service.EmailService;
import com.example.hackathon.service.Logwritter;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cleaner")
@CrossOrigin(origins = "*") // Allow all origins for development
public class FileCleanerController {

    @Autowired
    private DuplicateScannerService scannerService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/scan")
    public ResponseEntity<ScanResponseDto> scanDirectory(@RequestBody ScanRequestDto request) {
        ScanResponseDto response = new ScanResponseDto();
        
        try {
            // Initialize logs
            Logwritter.initLogFile();
            
            // Validate directory
            File dir = new File(request.getDirectoryPath());
            if (!dir.exists() || !dir.isDirectory()) {
                response.setMessage("Invalid directory path");
                response.setSuccess(false);
                return ResponseEntity.badRequest().body(response);
            }

            // Perform requested operation
            switch (request.getOperation()) {
                case DELETE_DUPLICATES:
                    scannerService.deleteDuplicatesOnly(request.getDirectoryPath(), request.isRecursive());
                    break;
                case CATEGORIZE_FILES:
                    scannerService.categorizeOnly(request.getDirectoryPath(), request.isRecursive());
                    break;
                case BOTH:
                    scannerService.scanDirectory(request.getDirectoryPath(), request.isRecursive());
                    break;
                case NONE:
                    response.setMessage("No operation performed");
                    response.setSuccess(true);
                    return ResponseEntity.ok(response);
            }

            response.setMessage("Operation completed successfully");
            response.setSuccess(true);
            response.setLogFilePath(Logwritter.getLogFilePath());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setMessage("Error during operation: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/send-email")
    public ResponseEntity<ScanResponseDto> sendLogEmail(@RequestBody EmailRequestDto request) {
        ScanResponseDto response = new ScanResponseDto();
        
        if (!request.isSendEmail() || request.getEmail() == null || request.getEmail().isEmpty()) {
            response.setMessage("Email not requested or invalid email address");
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            File logFile = Logwritter.getLogFile();
            if (logFile.exists()) {
                emailService.sendEmailWithAttachment(request.getEmail(), logFile);
                response.setMessage("Email sent successfully");
                response.setSuccess(true);
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Log file not found");
                response.setSuccess(false);
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.setMessage("Failed to send email: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}