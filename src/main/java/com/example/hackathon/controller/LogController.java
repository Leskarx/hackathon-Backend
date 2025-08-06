package com.example.hackathon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hackathon.dto.LogEntryDto;
import com.example.hackathon.service.Logwritter;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    @GetMapping
    public ResponseEntity<List<LogEntryDto>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String date) {
        try {
            List<LogEntryDto> logs = Logwritter.getStructuredLogs(action, date);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
