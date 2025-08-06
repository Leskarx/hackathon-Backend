package com.example.hackathon.service;

import java.io.File;
import java.io.IOException;
import java.util.*;
// import java.util.stream.Collectors;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import com.example.hackathon.config.RuleLoader;
import com.example.hackathon.dto.DeleteDuplicatesRequestDto;
import com.example.hackathon.dto.DuplicateFileDto;
import com.example.hackathon.dto.DuplicateGroupDto;
import com.example.hackathon.helper.GetfileExtension;
import com.example.hackathon.rules.CategorizationRule;
import com.example.hackathon.rules.CategorizationRuleLoader;
// import com.example.hackathon.service.Logwritter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DuplicateScannerService {
    private static final Map<String, String> EXTENSION_CATEGORIES = Map.of(
    "exe", "Installers",
    "pdf", "Documents",
    "docx", "Documents",
    "txt", "TextFiles",
    "png", "Images",
    "jpg", "Images",
    "jpeg", "Images",
    "mp4", "Videos",
    "mkv", "Videos"
);


    private final Map<String, List<File>> hashMap = new HashMap<>();
    private final GetfileExtension getFileExtension = new GetfileExtension();
    private List<CategorizationRule> metadataRules = new ArrayList<>();

    public DuplicateScannerService() {
        loadCategorizationRules(); // ✅ Load rules from JSON file at startup
    }

    private void loadCategorizationRules() {
        try {
            File jsonFile = new File("metadata_rules.json"); // ✅ JSON must be in root dir or provide full path
            if (jsonFile.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                metadataRules = objectMapper.readValue(jsonFile, new TypeReference<List<CategorizationRule>>() {});
                System.out.println("✅ Metadata categorization rules loaded: " + metadataRules.size());
            } else {
                System.out.println("⚠️ metadata_rules.json not found. Skipping metadata rules.");
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading metadata rules: " + e.getMessage());
        }
    }

    public void scanDirectory(String path, boolean recursive) {
        hashMap.clear(); // Reset before every new scan

        Collection<File> files = FileUtils.listFiles(new File(path), null, recursive);
        for (File file : files) {
            try {
                String fileHash = DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));
                hashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(file);
            } catch (IOException e) {
                System.out.println("❌ Failed to read file: " + file.getAbsolutePath());
            }
        }

        deleteDuplicates();
        categorizeApplications(path);
    }

    public void deleteDuplicatesOnly(String path, boolean recursive) {
        hashMap.clear();

        Collection<File> files = FileUtils.listFiles(new File(path), null, recursive);
        for (File file : files) {
            try {
                String fileHash = DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));
                hashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(file);
            } catch (IOException e) {
                System.out.println("❌ Failed to read file: " + file.getAbsolutePath());
            }
        }

        deleteDuplicates();
    }

    public void categorizeOnly(String path, boolean recursive) {
        categorizeApplications(path);
    }

  // Update DuplicateScannerService.java
// Change deleteDuplicates() to return a result instead of using Scanner

private Map<String, List<String>> deleteDuplicates() {
    Map<String, List<String>> results = new HashMap<>();
    results.put("deleted", new ArrayList<>());
    results.put("errors", new ArrayList<>());
    
    int groupIndex = 1;
    for (List<File> duplicates : hashMap.values()) {
        if (duplicates.size() > 1) {
            // For API, we'll keep the first file and delete others
            // (Frontend can implement more sophisticated selection)
            File fileToKeep = duplicates.get(0);
            
            for (int i = 1; i < duplicates.size(); i++) {
                File file = duplicates.get(i);
                if (file.delete()) {
                    results.get("deleted").add(file.getAbsolutePath());
                    Logwritter.write("🗑️ Deleted: " + file.getAbsolutePath());
                } else {
                    results.get("errors").add(file.getAbsolutePath());
                }
            }
        }
    }
    
    return results;
}

    public String categorizeFile(File file, LoggerService logger) {
        String fileName = file.getName().toLowerCase();
    
        // Step 1: JSON Rule Matching
        List<CategorizationRule> rules = RuleLoader.loadRules("rules.json");
        for (CategorizationRule rule : rules) {
            if (fileName.contains(rule.getMatch().toLowerCase())) {
                logger.log("📄 JSON Rule match → " + rule.getCategory());
                return rule.getCategory();
            }
        }
    
        // Step 2: Extension-based Matching
        String extension = new GetfileExtension().getFileExtension(file);
        String category = EXTENSION_CATEGORIES.get(extension);
        if (category != null) {
            logger.log("🧩 Extension match → " + category);
            return category;
        }
    
        // Step 3: Tika Content-Based Matching
        try {
            Tika tika = new Tika();
            String detectedType = tika.detect(file);
            if (detectedType.contains("image")) {
                logger.log("🖼️ Tika match → Images");
                return "Images";
            }
        } catch (Exception e) {
            logger.log("❌ Tika error: " + e.getMessage());
        }
    
        return "Others";
    }
    
    

private void categorizeApplications(String basePath) {
    Collection<File> files = FileUtils.listFiles(new File(basePath), null, true);
    LoggerService logger = new LoggerService(); // Create logger once

    for (File file : files) {
        String category = categorizeFile(file, logger); // 🔄 Use advanced categorization here

        if (category != null && !category.isEmpty()) {
            File targetDir = new File(basePath + File.separator + category);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            File dest = new File(targetDir, file.getName());
            try {
                FileUtils.moveFile(file, dest);
                System.out.println("➡️ Moved: " + file.getName() + " → " + category + "/");
                Logwritter.write("➡️ Moved: " + file.getName() + " → " + category + "/");
            } catch (IOException e) {
                System.out.println("❌ Failed to move: " + file.getAbsolutePath());
            }
        }
    }
}


    private String getCategoryFromRules(File file, String extension) {
        // ✅ First try metadata rules
        for (CategorizationRule rule : metadataRules) {
            if (file.getName().toLowerCase().contains(rule.getMatch().toLowerCase())) {
                return rule.getCategory();
            }
        }

        // ✅ Then fall back to extension-based
        Map<String, String> CATEGORY_RULES = Map.of(
            "exe", "Executables",
            "pdf", "Documents",
            "docx", "Documents",
            "txt", "TextFiles",
            "png", "Images",
            "jpg", "Images",
            "jpeg", "Images",
            "mp4", "Videos",
            "mkv", "Videos"
        );

        return CATEGORY_RULES.getOrDefault(extension, "Others");
    }

    public List<DuplicateGroupDto> findDuplicates(String path, boolean recursive) {
    hashMap.clear(); // Reset before new scan
    List<DuplicateGroupDto> duplicateGroups = new ArrayList<>();

    Collection<File> files = FileUtils.listFiles(new File(path), null, recursive);
    for (File file : files) {
        try {
            String fileHash = DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));
            hashMap.computeIfAbsent(fileHash, k -> new ArrayList<>()).add(file);
        } catch (IOException e) {
            System.out.println("❌ Failed to read file: " + file.getAbsolutePath());
        }
    }

    // Convert to DTOs for response
    for (Map.Entry<String, List<File>> entry : hashMap.entrySet()) {
        if (entry.getValue().size() > 1) {
            DuplicateGroupDto group = new DuplicateGroupDto();
            group.setHash(entry.getKey());
            
            List<DuplicateFileDto> fileDtos = new ArrayList<>();
            for (File file : entry.getValue()) {
                DuplicateFileDto fileDto = new DuplicateFileDto();
                fileDto.setPath(file.getAbsolutePath());
                fileDto.setSize(file.length());
                fileDto.setLastModified(new Date(file.lastModified()).toString());
                fileDto.setSelected(false); // Default not selected
                fileDtos.add(fileDto);
            }
            group.setFiles(fileDtos);
            duplicateGroups.add(group);
        }
    }

    return duplicateGroups;
}

public Map<String, List<String>> processDuplicateDeletion(DeleteDuplicatesRequestDto request) {
    Map<String, List<String>> results = new HashMap<>();
    results.put("deleted", new ArrayList<>());
    results.put("errors", new ArrayList<>());

    for (DuplicateGroupDto group : request.getDuplicateGroups()) {
        // Find which files are marked to keep (selected)
        List<DuplicateFileDto> filesToKeep = group.getFiles().stream()
                .filter(DuplicateFileDto::isSelected)
                .collect(Collectors.toList());

        // If none selected, keep the first one by default
        if (filesToKeep.isEmpty()) {
            filesToKeep = Collections.singletonList(group.getFiles().get(0));
        }

        // Delete all files not marked to keep
        for (DuplicateFileDto fileDto : group.getFiles()) {
            if (!filesToKeep.contains(fileDto)) {
                File file = new File(fileDto.getPath());
                if (file.delete()) {
                    results.get("deleted").add(file.getAbsolutePath());
                    Logwritter.write("🗑️ Deleted: " + file.getAbsolutePath());
                } else {
                    results.get("errors").add(file.getAbsolutePath());
                }
            }
        }
    }

    return results;
}
}
