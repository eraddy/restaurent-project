package com.epam.edai.run8.team11.service.presignedimage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresignedImageService {

    private final String bucketName = "team11-asset-bucket";
    private final S3Presigner s3Presigner;

    /**
     * Generate a presigned URL for a given file in the S3 bucket.
     * @param filename The S3 key (filename) of the dish image in the bucket.
     * @return Temporary presigned URL valid for a short duration.
     */
    public String generatePresignedUrlForImage(String filename) {
        try {
            // Build S3 GetObjectRequest
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .build();

            // Set up PresignRequest with a time duration
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(15)) // URL valid for 15 minutes
                    .build();

            // Generate the presigned URL
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            URL presignedUrl = presignedRequest.url();

            return presignedUrl.toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL for image: {}", filename, e);
        }
        return "";
    }
}

