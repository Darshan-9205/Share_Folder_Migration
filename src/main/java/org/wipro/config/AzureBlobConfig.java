package org.wipro.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AzureBlobConfig {

    @ConfigProperty(name = "azure.storage.account-name")
    String accountName;

    @ConfigProperty(name = "azure.storage.account-key")
    String accountKey;

    @ConfigProperty(name = "azure.storage.container-name")
    String containerName;

    @Produces
    @ApplicationScoped
    public BlobContainerClient blobContainerClient() {

        String connectionString = String.format(
                "DefaultEndpointsProtocol=https;"
                        + "AccountName=%s;"
                        + "AccountKey=%s;"
                        + "EndpointSuffix=core.windows.net",
                accountName,
                accountKey);

        return new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
    }
}