package com.example.hackathon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hackathon.dto.ConfigDto;
import com.example.hackathon.service.DuplicateScannerService;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigController {

    @Autowired
    private DuplicateScannerService scannerService;

    @GetMapping
    public ResponseEntity<ConfigDto> getConfig() {
        ConfigDto config = new ConfigDto();
        config.setScanPaths(scannerService.getScanPaths());
        config.setRules(scannerService.getAllRules());
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<?> updateConfig(@RequestBody ConfigDto config) {
        try {
            scannerService.updateConfig(config);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update config: " + e.getMessage());
        }
    }
}