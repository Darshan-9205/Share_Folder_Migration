package org.wipro.model;

public enum MigrationSource {

    LOCAL_SHARE("local-share"),

    AZURE_SHARE("azure-share"),

    ONEDRIVE_SHARE("onedrive-share"),

    GDRIVE_SHARE("gdrive-share"),

    SHAREPOINT_SHARE("sharepoint-share");

    private final String folderName;

    MigrationSource(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}