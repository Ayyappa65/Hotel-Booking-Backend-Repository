package com.ayyappa.hotelbooking.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayyappa.hotelbooking.payload.response.MessageResponse;
import com.ayyappa.hotelbooking.service.S3Service;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // Upload
    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam(value = "folder", required = false) String folderName) {
        try {
            logger.info("Received file for upload: {} to folder: {}", file.getOriginalFilename(), folderName);
            String url = s3Service.uploadFile(file, folderName);
            return ResponseEntity.ok(new MessageResponse("Uploaded Successfully: " + url));
        } catch (IOException e) {
            logger.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Upload failed: " + e.getMessage()));
       }
    }


    // Get All File Keys
    @GetMapping
    public ResponseEntity<List<String>> listAllFiles() {
        List<String> files = s3Service.listAllFiles();
        return ResponseEntity.ok(files);
    }

    @PostMapping("/create-folder")
    public ResponseEntity<MessageResponse> createFolder(@RequestParam("name") String folderName) {
        try {
            logger.info("Creating folder: {}", folderName);
            s3Service.createFolder(folderName);
            return ResponseEntity.ok(new MessageResponse("Folder created: " + folderName));
        } catch (Exception e) {
            logger.error("Error creating folder: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to create folder.\nError: " + e.getMessage()));
        }
   }


    // Get File URL
    @GetMapping("/view/**")
    public ResponseEntity<byte[]> viewFile(HttpServletRequest request) {
        String prefix = "/api/v1/files/view/";
        String fullPath = request.getRequestURI();
        String key = fullPath.substring(fullPath.indexOf(prefix) + prefix.length());

        return s3Service.getFile(key);
    }

    // Delete File
    @DeleteMapping
    public ResponseEntity<MessageResponse> deleteFile(@RequestParam String key) {
        try {
            logger.info("Deleting file with key: {}", key);
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("File key must not be empty.");
            }
            s3Service.deleteFile(key);
            return ResponseEntity.ok(new MessageResponse("File deleted: " + key));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Delete failed: " + e.getMessage()));
        }
    }

    // Delete Folder
    @DeleteMapping("/folder")
    public ResponseEntity<MessageResponse> deleteFolder(@RequestParam String key) {
        try {
            logger.info("Deleting folder with key: {}", key);
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("Folder key must not be empty.");
            }
            s3Service.deleteFolder(key);
            return ResponseEntity.ok(new MessageResponse("Folder deleted: " + key));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Delete failed: " + e.getMessage()));
        }
    }
}
