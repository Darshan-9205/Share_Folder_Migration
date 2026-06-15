package org.wipro.service;

import org.wipro.model.MigrationSource;
import org.wipro.model.UploadResponse;
import org.wipro.util.DocumentIdGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class S3FileService {

        @Inject
        S3Client s3Client;

        @ConfigProperty(name = "aws.s3.bucket-name")
        String bucketName;

        @ConfigProperty(name = "aws.region")
        String region;

        /**
         * Upload folder/share-folder files.
         */
        public UploadResponse uploadFile(
                        String filePath,
                        String relativePath,
                        MigrationSource migrationSource,
                        String uploadedBy) {

                File file = validateFile(filePath);

                String s3Key = buildS3Key(
                                migrationSource,
                                relativePath);

                return uploadToS3(
                                file,
                                s3Key,
                                migrationSource,
                                uploadedBy,
                                "FOLDER",
                                null);
        }

        /**
         * Upload single document.
         */
        public UploadResponse uploadDocument(
                        String filePath,
                        String fileName,
                        MigrationSource migrationSource,
                        String uploadedBy) {

                File file = validateFile(filePath);

                String s3Key = migrationSource.getFolderName()
                                + "/documents/"
                                + fileName;

                return uploadToS3(
                                file,
                                s3Key,
                                migrationSource,
                                uploadedBy,
                                "FILE",
                                null);
        }

        /**
         * Upload multiple documents.
         */
        public List<UploadResponse> uploadMultipleDocuments(
                        List<String> filePaths,
                        MigrationSource migrationSource,
                        String uploadedBy) {

                return filePaths.stream()
                                .map(path -> uploadDocument(
                                                path,
                                                new File(path).getName(),
                                                migrationSource,
                                                uploadedBy))
                                .toList();
        }

        /**
         * Centralized AWS S3 Upload Engine.
         */
        private UploadResponse uploadToS3(
                        File file,
                        String s3Key,
                        MigrationSource migrationSource,
                        String uploadedBy,
                        String uploadType,
                        String sourceUploadDate) {

                String documentId = DocumentIdGenerator.generateDocumentId();

                LocalDateTime migrationDate = LocalDateTime.now();

                Map<String, String> metadata = buildMetadata(
                                file,
                                uploadedBy,
                                migrationSource,
                                documentId,
                                uploadType,
                                sourceUploadDate,
                                migrationDate);

                String tags = buildTags(
                                migrationSource,
                                uploadType);

                PutObjectRequest request = PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(s3Key)
                                .contentType(detectContentType(file))
                                .metadata(metadata)
                                .tagging(tags)
                                .build();

                s3Client.putObject(
                                request,
                                RequestBody.fromFile(file));

                return new UploadResponse(
                                file.getName(),
                                s3Key,
                                buildFileUrl(s3Key),
                                uploadedBy,
                                documentId,
                                migrationDate);
        }

        /**
         * Build metadata.
         */
        private Map<String, String> buildMetadata(
                        File file,
                        String uploadedBy,
                        MigrationSource migrationSource,
                        String documentId,
                        String uploadType,
                        String sourceUploadDate,
                        LocalDateTime migrationDate) {

                Map<String, String> metadata = new HashMap<>();

                metadata.put("document-id", documentId);
                metadata.put("original-file-name", file.getName());
                metadata.put("original-file-path",
                                file.getAbsolutePath().replace("\\", "/"));
                metadata.put("file-size-bytes",
                                String.valueOf(file.length()));
                metadata.put("file-extension",
                                getFileExtension(file.getName()));
                metadata.put("uploaded-by", uploadedBy);
                metadata.put("migration-source",
                                migrationSource.name());
                metadata.put("migration-date",
                                migrationDate.toString());
                metadata.put("upload-type",
                                uploadType);

                if (sourceUploadDate != null &&
                                !sourceUploadDate.isBlank()) {

                        metadata.put(
                                        "source-upload-date",
                                        sourceUploadDate);
                }

                return metadata;
        }

        /**
         * Build S3 tags.
         */
        private String buildTags(
                        MigrationSource migrationSource,
                        String uploadType) {

                Map<String, String> tags = Map.of(
                                "migrationType", migrationSource.name(),
                                "application", "FILE_MIGRATION",
                                "environment", "POC",
                                "migrationStatus", "COMPLETED",
                                "uploadType", uploadType);

                return tags.entrySet()
                                .stream()
                                .map(entry -> URLEncoder.encode(
                                                entry.getKey(),
                                                StandardCharsets.UTF_8)
                                                + "=" +
                                                URLEncoder.encode(
                                                                entry.getValue(),
                                                                StandardCharsets.UTF_8))
                                .collect(Collectors.joining("&"));
        }

        /**
         * Build S3 object key.
         */
        private String buildS3Key(
                        MigrationSource source,
                        String relativePath) {

                return source.getFolderName()
                                + "/"
                                + relativePath;
        }

        /**
         * Validate file.
         */
        private File validateFile(
                        String filePath) {

                File file = new File(filePath);

                if (!file.exists()) {
                        throw new IllegalArgumentException(
                                        "File does not exist: "
                                                        + filePath);
                }

                if (!file.isFile()) {
                        throw new IllegalArgumentException(
                                        "Provided path is not a file: "
                                                        + filePath);
                }

                return file;
        }

        /**
         * Detect content type.
         */
        private String detectContentType(
                        File file) {

                try {

                        String contentType = Files.probeContentType(
                                        file.toPath());

                        return contentType != null
                                        ? contentType
                                        : "application/octet-stream";

                } catch (IOException ex) {

                        return "application/octet-stream";
                }
        }

        /**
         * Generate file URL.
         */
        private String buildFileUrl(
                        String s3Key) {

                String encodedKey = URLEncoder.encode(
                                s3Key,
                                StandardCharsets.UTF_8)
                                .replace("+", "%20");

                return String.format(
                                "https://%s.s3.%s.amazonaws.com/%s",
                                bucketName,
                                region,
                                encodedKey);
        }

        /**
         * Extract file extension.
         */
        private String getFileExtension(
                        String fileName) {

                int lastDot = fileName.lastIndexOf('.');

                return lastDot == -1
                                ? ""
                                : fileName.substring(lastDot + 1);
        }
}