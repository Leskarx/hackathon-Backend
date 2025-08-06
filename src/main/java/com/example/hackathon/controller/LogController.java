package com.example.hackathon.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // If action is "ALL", ignore action filtering
            List<LogEntryDto> logs;
            if ("ALL".equalsIgnoreCase(action)) {
                logs = Logwritter.getStructuredLogs(null, date);  // null action = get all
            } else {
                logs = Logwritter.getStructuredLogs(action, date);
            }
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}