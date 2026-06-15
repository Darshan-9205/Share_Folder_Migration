package org.wipro.util;

import java.util.UUID;

public final class DocumentIdGenerator {

    private DocumentIdGenerator() {
    }

    public static String generateDocumentId() {

        return "DOC-"
                + UUID.randomUUID()
                        .toString()
                        .toUpperCase();
    }
}