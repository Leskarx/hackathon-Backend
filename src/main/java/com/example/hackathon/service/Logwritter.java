package com.example.hackathon.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.hackathon.dto.LogEntryDto;

@Service
public class Logwritter {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE_NAME = "appcleaner-log.txt";

    static {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    // Call this once before writing logs
    public static void initLogFile() {
        File logFile = new File(LOG_DIR, LOG_FILE_NAME);
        if (logFile.exists()) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File archivedFile = new File(LOG_DIR, "appcleaner-log-" + timestamp + ".txt");

            boolean renamed = logFile.renameTo(archivedFile);
            if (renamed) {
                System.out.println("🗂️ Archived previous log file as: " + archivedFile.getName());
            } else {
                System.out.println("⚠️ Failed to archive existing log file.");
            }
        }
    }

    public static void write(String message) {
        try (FileWriter writer = new FileWriter(new File(LOG_DIR, LOG_FILE_NAME), true)) {
            writer.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - " + message + "\n");
        } catch (IOException e) {
            System.out.println("❌ Failed to write to log file: " + e.getMessage());
        }
    }

    public static String getLogFilePath() {
        return new File(LOG_DIR, LOG_FILE_NAME).getAbsolutePath();
    }

    public static File getLogFile() {
        return new File(LOG_DIR, LOG_FILE_NAME);
    }
    // Add this method to your Logwritter class

public static List<LogEntryDto> getStructuredLogs(String actionFilter, String dateFilter) {
    List<LogEntryDto> logs = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    try (Stream<String> stream = Files.lines(Paths.get(LOG_DIR, LOG_FILE_NAME))) {
        stream.forEach(line -> {
            try {
                String[] parts = line.split(" - ", 2);
                if (parts.length == 2) {
                    LocalDateTime timestamp = LocalDateTime.parse(parts[0], formatter);
                    String logContent = parts[1];
                    
                    LogEntryDto entry = new LogEntryDto();
                    entry.setTimestamp(timestamp);
                    
                    if (logContent.startsWith("🗑️")) {
                        entry.setAction("DELETE");
                        entry.setDetails(logContent.substring(2).trim());
                    } else if (logContent.startsWith("➡️")) {
                        entry.setAction("MOVE");
                        entry.setDetails(logContent.substring(2).trim());
                    } else {
                        entry.setAction("INFO");
                        entry.setDetails(logContent.trim());
                    }
                    
                    // Apply filters
                    boolean matchesAction = actionFilter == null || 
                                          entry.getAction().equalsIgnoreCase(actionFilter);
                    boolean matchesDate = dateFilter == null || 
                                        entry.getTimestamp().toLocalDate().toString().equals(dateFilter);
                    
                    if (matchesAction && matchesDate) {
                        logs.add(entry);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error parsing log line: " + line);
            }
        });
    } catch (IOException e) {
        System.out.println("Error reading log file: " + e.getMessage());
    }
    
    return logs;
}
    
}
