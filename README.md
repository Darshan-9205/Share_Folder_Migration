# Share Folder Migration Platform

## Overview

The Share Folder Migration Platform is an enterprise-grade file migration solution designed to securely and efficiently migrate files and folders from multiple source systems into Amazon S3 Object Storage. The platform provides a unified migration framework that supports local file systems, cloud storage providers, and shared drives while preserving folder hierarchy, metadata, and audit information throughout the migration process.

Built using Java, Quarkus, GraphQL, and cloud-native technologies, the solution enables organizations to streamline large-scale content migration initiatives with reliability, scalability, and extensibility.

---

## Business Objective

Organizations often store critical business documents across multiple platforms, including local shared folders, Azure Blob Storage, Google Drive, OneDrive, and network drives. Managing and consolidating these files into a centralized object storage solution can be challenging.

This platform addresses that challenge by providing a single migration service capable of transferring content from various storage providers into Amazon S3 while maintaining data integrity and traceability.

---

## Supported Migration Sources

### Local Storage
- Local File System
- Shared Network Drives
- Enterprise Shared Folders

### Cloud Storage
- Azure Blob Storage
- Google Drive *(Planned)*
- Microsoft OneDrive *(Planned)*

### Target Storage
- Amazon S3 Object Storage

---

## Core Capabilities

### File Migration
- Single file migration
- Multiple file migration
- Bulk document migration
- Large file support

### Folder Migration
- Recursive folder migration
- Nested folder structure preservation
- Complete directory migration

### Metadata Management
- Unique document identification
- Original file name tracking
- Original file path tracking
- Migration source tracking
- Upload user tracking
- Migration timestamp tracking
- File type and extension identification

### AWS S3 Integration
- Automated object creation
- Object metadata management
- Custom tagging support
- Content type detection
- Secure object URL generation

### Enterprise Features
- Cloud-to-cloud migration support
- Cross-platform migration capability
- Extensible migration architecture
- Centralized migration workflow
- Audit-ready migration records

---

## Technology Stack

### Backend Technologies
- Java 21
- Quarkus

### API Layer
- GraphQL (SmallRye GraphQL)

### Cloud Services
- Amazon S3
- Azure Blob Storage

### Cloud SDKs
- AWS SDK for Java v2
- Azure Storage Blob SDK

### Build & Dependency Management
- Maven

### Dependency Injection
- CDI (Contexts and Dependency Injection)

---

## Solution Architecture

```text
+---------------------------+
|      Source Systems       |
+---------------------------+
| Local File System         |
| Shared Network Drives     |
| Azure Blob Storage        |
| Google Drive (Planned)    |
| Microsoft OneDrive        |
+-------------+-------------+
              |
              v
+---------------------------+
| Share Folder Migration    |
| Platform                  |
+---------------------------+
| GraphQL APIs              |
| Migration Services        |
| Metadata Engine           |
| Validation Layer          |
+-------------+-------------+
              |
              v
+---------------------------+
| Amazon S3 Object Storage  |
+---------------------------+
```

---

## Key Migration Workflows

### Local Storage to Amazon S3
- Single file migration
- Multiple file migration
- Recursive folder migration

### Azure Blob Storage to Amazon S3
- Single blob migration
- Multiple blob migration
- Folder migration
- Container-wide migration

### Future Integrations
- Google Drive to Amazon S3
- OneDrive to Amazon S3
- Shared Drive to Amazon S3

---

## GraphQL APIs

### Upload Single Local File

```graphql
mutation {
  uploadFile(
    filePath: "C:/Documents/sample.pdf"
    uploadedBy: "Darshan S"
  ) {
    documentId
    s3Key
    url
  }
}
```

### Upload Local Folder

```graphql
mutation {
  uploadLocalFolder(
    localFolderPath: "C:/Documents"
    uploadedBy: "Darshan S"
  ) {
    documentId
    s3Key
  }
}
```

### Migrate Azure Blob

```graphql
mutation {
  migrateAzureBlob(
    blobPath: "documents/sample.pdf"
    uploadedBy: "Darshan S"
  ) {
    documentId
    s3Key
    url
  }
}
```

### Migrate Azure Container

```graphql
mutation {
  migrateAzureContainer(
    uploadedBy: "Darshan S"
  ) {
    documentId
    s3Key
  }
}
```

---

## Configuration

### AWS Configuration

```properties
aws.access-key-id=
aws.secret-access-key=
aws.region=
aws.s3.bucket-name=
```

### Azure Configuration

```properties
azure.storage.account-name=
azure.storage.account-key=
azure.storage.container-name=
```

---

## Migration Lifecycle

1. User initiates migration through GraphQL APIs.
2. Source files and folders are validated.
3. Metadata is extracted and generated.
4. Content is downloaded or accessed from the source system.
5. Files are uploaded to Amazon S3.
6. Metadata and tags are attached to S3 objects.
7. Migration details are recorded.
8. Success response is returned to the user.

---

## Security & Compliance

- Secure communication using TLS 1.2+
- AWS IAM-based access control
- Azure Storage Account authentication
- Metadata-based auditing
- Controlled access to cloud credentials
- Secure object storage practices

---

## Future Roadmap

- Google Drive Integration
- Microsoft OneDrive Integration
- Shared Drive Integration
- Migration Dashboard
- Migration Analytics and Reporting
- Parallel Processing for Large Migrations
- Scheduled Migration Jobs
- Notification and Alert Framework
- Migration Retry and Recovery Mechanism
- Audit and Compliance Reporting

---

## Author

**Darshan S**  
Software Engineer | Wipro Limited

### Skills
- Java
- Quarkus
- GraphQL
- AWS S3
- Azure Blob Storage
- REST APIs
- Cloud Migration Solutions
- Enterprise Application Development

---

## License

This project is intended for learning, research, and enterprise migration use cases.
