package org.wipro.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.wipro.model.MigrationSource;
import org.wipro.model.UploadResponse;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AzureBlobService {

    @Inject
    BlobContainerClient blobContainerClient;

    @Inject
    S3FileService s3FileService;

    /**
     * Migrate complete Azure container.
     */
    public List<UploadResponse> migrateAzureContainer(
            String uploadedBy) {

        List<UploadResponse> responses = new ArrayList<>();

        for (BlobItem blobItem : blobContainerClient.listBlobs()) {

            String blobPath = blobItem.getName();

            if (blobPath.endsWith("/")) {
                continue;
            }

            responses.add(
                    migrateBlob(
                            blobPath,
                            uploadedBy,
                            true));
        }

        return responses;
    }

    /**
     * Migrate specific Azure folder.
     */
    public List<UploadResponse> migrateAzureFolder(
            String folderPath,
            String uploadedBy) {

        List<UploadResponse> responses = new ArrayList<>();

        ListBlobsOptions options = new ListBlobsOptions()
                .setPrefix(folderPath + "/");

        for (BlobItem blobItem : blobContainerClient.listBlobs(options, null)) {

            String blobPath = blobItem.getName();

            if (blobPath.endsWith("/")) {
                continue;
            }

            responses.add(
                    migrateBlob(
                            blobPath,
                            uploadedBy,
                            true));
        }

        return responses;
    }

    /**
     * Migrate single Azure document.
     */
    public UploadResponse uploadAzureDocument(
            String blobPath,
            String uploadedBy) {

        return migrateBlob(
                blobPath,
                uploadedBy,
                false);
    }

    /**
     * Migrate multiple Azure documents.
     */
    public List<UploadResponse> uploadMultipleAzureDocuments(
            List<String> blobPaths,
            String uploadedBy) {

        return blobPaths.stream()
                .map(path -> uploadAzureDocument(
                        path,
                        uploadedBy))
                .toList();
    }

    /**
     * Centralized Azure → S3 migration logic.
     */
    private UploadResponse migrateBlob(
            String blobPath,
            String uploadedBy,
            boolean preserveFolderStructure) {

        File tempFile = null;

        try {

            tempFile = File.createTempFile(
                    "azure-migration-",
                    ".tmp");

            blobContainerClient
                    .getBlobClient(blobPath)
                    .downloadToFile(
                            tempFile.getAbsolutePath(),
                            true);

            if (preserveFolderStructure) {

                return s3FileService.uploadFile(
                        tempFile.getAbsolutePath(),
                        blobPath,
                        MigrationSource.AZURE_SHARE,
                        uploadedBy);
            }

            String fileName = new File(blobPath).getName();

            return s3FileService.uploadDocument(
                    tempFile.getAbsolutePath(),
                    fileName,
                    MigrationSource.AZURE_SHARE,
                    uploadedBy);

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Failed to migrate Azure blob: "
                            + blobPath,
                    ex);

        } finally {

            if (tempFile != null) {

                try {
                    Files.deleteIfExists(
                            tempFile.toPath());
                } catch (Exception ignored) {
                }
            }
        }
    }
}