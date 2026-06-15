package org.wipro.service;

import org.wipro.model.MigrationSource;
import org.wipro.model.UploadResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LocalUploadService {

        @Inject
        S3FileService s3FileService;

        /**
         * Upload an entire local folder recursively.
         */
        public List<UploadResponse> uploadLocalFolder(
                        String localFolderPath,
                        String uploadedBy) {

                File rootFolder = validatePath(
                                localFolderPath,
                                true);

                List<UploadResponse> responses = new ArrayList<>();

                uploadRecursively(
                                rootFolder,
                                rootFolder,
                                uploadedBy,
                                responses);

                return responses;
        }

        /**
         * Upload a single local file.
         */
        public UploadResponse uploadLocalDocument(
                        String filePath,
                        String uploadedBy) {

                File file = validatePath(
                                filePath,
                                false);

                return s3FileService.uploadDocument(
                                file.getAbsolutePath(),
                                file.getName(),
                                MigrationSource.LOCAL_SHARE,
                                uploadedBy);
        }

        /**
         * Upload multiple files.
         */
        public List<UploadResponse> uploadMultipleLocalDocuments(
                        List<String> filePaths,
                        String uploadedBy) {

                return filePaths.stream()
                                .map(path -> uploadLocalDocument(
                                                path,
                                                uploadedBy))
                                .toList();
        }

        /**
         * Recursively traverse folders and upload files.
         */
        private void uploadRecursively(
                        File rootFolder,
                        File current,
                        String uploadedBy,
                        List<UploadResponse> responses) {

                if (current.isDirectory()) {

                        File[] children = current.listFiles();

                        if (children == null) {
                                return;
                        }

                        for (File child : children) {
                                uploadRecursively(
                                                rootFolder,
                                                child,
                                                uploadedBy,
                                                responses);
                        }

                        return;
                }

                String relativePath = rootFolder.getName()
                                + "/"
                                + rootFolder.toURI()
                                                .relativize(current.toURI())
                                                .getPath();

                UploadResponse response = s3FileService.uploadFile(
                                current.getAbsolutePath(),
                                relativePath,
                                MigrationSource.LOCAL_SHARE,
                                uploadedBy);

                responses.add(response);
        }

        /**
         * Generic path validation.
         *
         * @param path              path to validate
         * @param directoryExpected true for folder, false for file
         */
        private File validatePath(
                        String path,
                        boolean directoryExpected) {

                File file = new File(path);

                if (!file.exists()) {
                        throw new IllegalArgumentException(
                                        "Path does not exist: " + path);
                }

                if (directoryExpected && !file.isDirectory()) {
                        throw new IllegalArgumentException(
                                        "Expected a folder but found a file: " + path);
                }

                if (!directoryExpected && !file.isFile()) {
                        throw new IllegalArgumentException(
                                        "Expected a file but found a folder: " + path);
                }

                return file;
        }
}