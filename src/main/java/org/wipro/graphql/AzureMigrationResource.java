package org.wipro.graphql;

import org.wipro.model.UploadResponse;
import org.wipro.service.AzureBlobService;

import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;

import java.util.List;

@GraphQLApi
public class AzureMigrationResource {

        @Inject
        AzureBlobService azureBlobService;

        @Mutation
        public UploadResponse migrateAzureBlob(
                        String blobPath,
                        String uploadedBy) {

                return azureBlobService.uploadAzureDocument(
                                blobPath,
                                uploadedBy);
        }

        @Mutation
        public List<UploadResponse> migrateAzureBlobs(
                        List<String> blobPaths,
                        String uploadedBy) {

                return azureBlobService
                                .uploadMultipleAzureDocuments(
                                                blobPaths,
                                                uploadedBy);
        }

        @Mutation
        public List<UploadResponse> migrateAzureContainer(
                        String uploadedBy) {

                return azureBlobService
                                .migrateAzureContainer(
                                                uploadedBy);
        }

        @Mutation
        public List<UploadResponse> migrateAzureFolder(
                        String folderPath,
                        String uploadedBy) {

                return azureBlobService
                                .migrateAzureFolder(
                                                folderPath,
                                                uploadedBy);
        }
}