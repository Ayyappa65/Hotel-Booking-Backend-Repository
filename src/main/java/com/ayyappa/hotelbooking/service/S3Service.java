package com.ayyappa.hotelbooking.service;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Service class for handling AWS S3 file operations.
 */
@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to the configured S3 bucket and returns the public URL.
     *
     * @param file Multipart file to upload
     * @return Public URL of the uploaded file
     * @throws IOException if file reading or upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("File must not be null or empty.");
    }

    String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

    logger.info("Uploading '{}' to bucket '{}'", uniqueFileName, bucketName);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
         PutObjectRequest request = new PutObjectRequest(
                bucketName,
                uniqueFileName,
                file.getInputStream(), 
                metadata
        );
        s3Client.putObject(request);
    } catch (SdkClientException | IOException e) {
        logger.error("S3 upload failed: {}", e.getMessage(), e);
        throw new IOException("Error uploading to S3", e);
    }

    String fileUrl = s3Client.getUrl(bucketName, uniqueFileName).toString();
    logger.info("Upload successful. File URL: {}", fileUrl);

    return fileUrl;
}


    /**
     * Generates a unique filename using UUID to avoid collisions.
     *
     * @param originalFilename Original file name
     * @return A unique file name
     */
    private String generateUniqueFileName(String originalFilename) {
        String cleanFilename = (originalFilename != null && !originalFilename.trim().isEmpty())
                ? originalFilename.trim().replaceAll("\\s+", "_")
                : "uploaded_file";

        return UUID.randomUUID() + "_" + cleanFilename;
    }
}
