package com.ayyappa.hotelbooking.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ayyappa.hotelbooking.service.S3Service;

/**
 * REST controller for file upload operations to AWS S3.
 */
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * Uploads a file to AWS S3 and returns the public URL.
     *
     * @param file Multipart file sent via form-data
     * @return ResponseEntity with status and message
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Received file upload request for file: {}", file.getOriginalFilename());
        try {
            String url = s3Service.uploadFile(file);
            logger.info("File uploaded successfully. Accessible at: {}", url);
            return ResponseEntity.ok("✅ File uploaded successfully.\nURL: " + url);
        } catch (IOException e) {
            logger.error("File upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Upload failed.\nError: " + e.getMessage());
        }
    }
}
