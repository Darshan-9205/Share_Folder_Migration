package org.wipro.graphql;

import org.wipro.model.MigrationSource;
import org.wipro.model.UploadResponse;
import org.wipro.service.LocalUploadService;
import org.wipro.service.S3FileService;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import java.io.File;
import java.util.List;

@GraphQLApi
public class LocalMigrationResource {

    @Inject
    S3FileService s3FileService;

    @Inject
    LocalUploadService localUploadService;

    // ---------------------------
    // Health Check
    // ---------------------------
    @Query
    public String health() {
        return "Backend is running";
    }

    // ---------------------------
    // Single File Upload
    // ---------------------------
    @Mutation
    public UploadResponse uploadFile(
            String filePath,
            String uploadedBy) {

        String fileName = new File(filePath).getName();
        String relativePath = "documents/" + fileName;

        return s3FileService.uploadFile(
                filePath,
                relativePath,
                MigrationSource.LOCAL_SHARE,
                uploadedBy);
    }

    // ---------------------------
    // Multiple Files Upload (FIXED)
    // ---------------------------
    @Mutation
    public List<UploadResponse> uploadMultipleFiles(
            List<String> filePaths,
            String uploadedBy) {

        return s3FileService.uploadMultipleDocuments(
                filePaths,
                MigrationSource.LOCAL_SHARE,
                uploadedBy);
    }

    // ---------------------------
    // Multiple Documents Upload (explicit alias API)
    // ---------------------------
    @Mutation
    public List<UploadResponse> uploadMultipleDocuments(
            List<String> filePaths,
            String uploadedBy) {

        return s3FileService.uploadMultipleDocuments(
                filePaths,
                MigrationSource.LOCAL_SHARE,
                uploadedBy);
    }

    // ---------------------------
    // Recursive Folder Upload
    // ---------------------------
    @Mutation
    public List<UploadResponse> uploadLocalFolder(
            String localFolderPath,
            String uploadedBy) {

        return localUploadService.uploadLocalFolder(
                localFolderPath,
                uploadedBy);
    }
}