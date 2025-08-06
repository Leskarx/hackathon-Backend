package com.example.hackathon.controller;

import com.example.hackathon.dto.*;
import com.example.hackathon.service.DuplicateScannerService;
import com.example.hackathon.service.EmailService;
import com.example.hackathon.service.Logwritter;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cleaner")
@CrossOrigin(origins = "*")
public class FileCleanerController {

    @Autowired
    private DuplicateScannerService scannerService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/scan")
    public ResponseEntity<ScanResponseDto> scanDirectory(@RequestBody ScanRequestDto request) {
        ScanResponseDto response = new ScanResponseDto();
        
        try {
            Logwritter.initLogFile();
            
            File dir = new File(request.getDirectoryPath());
            if (!dir.exists() || !dir.isDirectory()) {
                response.setMessage("Invalid directory path");
                response.setSuccess(false);
                return ResponseEntity.badRequest().body(response);
            }

            switch (request.getOperation()) {
                case DELETE_DUPLICATES:
                    List<DuplicateGroupDto> deleteDuplicates = scannerService.findDuplicates(
                        request.getDirectoryPath(), 
                        request.isRecursive()
                    );
                    response.setMessage("Duplicates found: " + deleteDuplicates.size() + " groups");
                    response.setSuccess(true);
                    response.setDuplicateGroups(deleteDuplicates);
                    break;
                    
                case CATEGORIZE_FILES:
                    scannerService.categorizeOnly(request.getDirectoryPath(), request.isRecursive());
                    response.setMessage("Files categorized successfully");
                    response.setSuccess(true);
                    break;
                    
                case BOTH:
                    List<DuplicateGroupDto> bothDuplicates = scannerService.findDuplicates(
                        request.getDirectoryPath(), 
                        request.isRecursive()
                    );
                    scannerService.categorizeOnly(request.getDirectoryPath(), request.isRecursive());
                    response.setMessage("Operation completed. Duplicates found: " + bothDuplicates.size() + " groups");
                    response.setSuccess(true);
                    response.setDuplicateGroups(bothDuplicates);
                    break;
                    
                case NONE:
                    response.setMessage("No operation performed");
                    response.setSuccess(true);
                    break;
            }

            response.setLogFilePath(Logwritter.getLogFilePath());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setMessage("Error during operation: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/duplicates")
    public ResponseEntity<List<DuplicateGroupDto>> findDuplicates(
            @RequestParam String directoryPath,
            @RequestParam(required = false, defaultValue = "false") boolean recursive) {
        
        try {
            File dir = new File(directoryPath);
            if (!dir.exists() || !dir.isDirectory()) {
                return ResponseEntity.badRequest().body(null);
            }

            List<DuplicateGroupDto> duplicates = scannerService.findDuplicates(directoryPath, recursive);
            return ResponseEntity.ok(duplicates);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/delete-duplicates")
    public ResponseEntity<Map<String, List<String>>> deleteSelectedDuplicates(
            @RequestBody DeleteDuplicatesRequestDto request) {
        
        try {
            Map<String, List<String>> results = scannerService.processDuplicateDeletion(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
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