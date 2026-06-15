package org.wipro.model;

import java.time.LocalDateTime;

public record UploadResponse(

                String documentName,

                String s3Key,

                String url,

                String uploadedBy,

                String documentId,

                LocalDateTime migrationDate

) {
}
