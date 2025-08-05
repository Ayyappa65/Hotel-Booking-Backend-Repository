package com.ayyappa.hotelbooking.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    //  CREATE (Upload)
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty.");
        }

        String key = (folderName != null && !folderName.isBlank())
            ? (folderName.endsWith("/") ? folderName : folderName + "/") + file.getOriginalFilename()
            : file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            s3Client.putObject(request);
        } catch (SdkClientException | IOException e) {
            logger.error("S3 upload failed: {}", e.getMessage(), e);
            throw new IOException("Error uploading to S3", e);
       }

        logger.info("Upload successful. File Key: {}", key);
        return key; // Return only the S3 key
   }


    //  READ: List all file keys
    public List<String> listAllFiles() {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        if (result.getObjectSummaries().isEmpty()) {
            logger.warn("No files found in bucket: {}", bucketName);
            return List.of();
        }
        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    //  CREATE FOLDER in S3 (creates a folder-like key)
    public void createFolder(String folderName) {
        if (folderName == null || folderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Folder name must not be empty.");
        }

        // Ensure folder name ends with "/"
        if (!folderName.endsWith("/")) {
            folderName += "/";
        }

        // Metadata for an empty object
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        try {
            PutObjectRequest request = new PutObjectRequest(
                bucketName,
                folderName,
                InputStream.nullInputStream(),
                metadata
           );
            s3Client.putObject(request);
            logger.info("Folder '{}' created successfully in bucket '{}'", folderName, bucketName);
        } catch (SdkClientException e) {
            logger.error("Failed to create folder: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating folder in S3", e);
        }
    }


    // READ: Get file content by key
    public ResponseEntity<byte[]> getFile(String key) {
        try {
            // Get object from S3
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));

            // Detect content type
            String contentType = s3Object.getObjectMetadata().getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream"; // Fallback type
            }

            byte[] bytes;
            try ( // Read content
                    InputStream inputStream = s3Object.getObjectContent()) {
                bytes = inputStream.readAllBytes();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                    .body(bytes);

        } catch (SdkClientException | IOException e) {
            throw new RuntimeException("Failed to get file: " + key, e);
        }
    }

    // DELETE: Delete file by key
    public void deleteFile(String key) {
        if (!s3Client.doesObjectExist(bucketName, key)) {
            throw new IllegalArgumentException("File not found: " + key);
        }
        s3Client.deleteObject(bucketName, key);
        logger.info("Deleted file: {}", key);
    }


    // DELETE: Delete folder by key
    public void deleteFolder(String key) {
        // Ensure the key ends with a slash to match folder structure
        if (!key.endsWith("/")) {
            key += "/";
        }

        // List all objects with the prefix
        ListObjectsV2Request listRequest = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(key);

        ListObjectsV2Result result;
    
        // Loop to handle pagination
        do {
            result = s3Client.listObjectsV2(listRequest);
            List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

            if (objectSummaries.isEmpty()) {
                logger.warn("Folder not found or already empty: {}", key);
                throw new IllegalArgumentException("Folder not found or already empty: " + key);
            }

            // Delete all objects under the folder
            for (S3ObjectSummary summary : objectSummaries) {
                s3Client.deleteObject(bucketName, summary.getKey());
                logger.info("Deleted: {}", summary.getKey());
            }

            // For paginated results
            listRequest.setContinuationToken(result.getNextContinuationToken());

        } while (result.isTruncated());
        logger.info("Deleted folder and all contents: {}", key);
    }
}
