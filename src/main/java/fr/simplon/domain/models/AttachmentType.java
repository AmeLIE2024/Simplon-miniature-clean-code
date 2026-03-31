package fr.simplon.domain.models;

import java.util.Arrays;

public enum AttachmentType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    LINK,
    POST,
    NONE,
    EXTERNAL;






    public static AttachmentType fromExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return NONE;
        }

        String extension = extractExtension(filename);


        if (isImageExtension(extension)) {
            return IMAGE;
        }


        if (isVideoExtension(extension)) {
            return VIDEO;
        }

        return NONE;
    }

    public static AttachmentType detectFromUrl(String mediaUrl) {
        if (mediaUrl == null || mediaUrl.isBlank()) {
            return NONE;
        }

        String lower = mediaUrl.toLowerCase();

        int queryIndex = lower.indexOf('?');
        if (queryIndex > -1) {
            lower = lower.substring(0, queryIndex);
        }

        if (lower.matches(".*\\.(jpg|jpeg|png|gif|webp)$")) {
            return IMAGE;
        }

        if (lower.matches(".*\\.(mp4|webm|wav|avi|mov|mkv)$")) {
            return VIDEO;
        }

        if (mediaUrl.startsWith("https://") || mediaUrl.startsWith("http://")) {
            return EXTERNAL;
        }

        return LINK;
    }


    private static String extractExtension(String filename) {
        if (!filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }


    private static boolean isImageExtension(String extension) {
        return Arrays.stream(ImageExtension.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(extension));
    }


    private static boolean isVideoExtension(String extension) {
        return Arrays.stream(VideoExtension.values())
                .anyMatch(e -> e.name().equalsIgnoreCase(extension));
    }

    public boolean isImage() {
        return this == IMAGE;
    }


    public boolean isVideo() {
        return this == VIDEO;
    }


    public boolean isDocument() {
        return this == DOCUMENT;
    }


    public boolean isLink() {
        return this == LINK;
    }

    public boolean isExternal() {
        return this == EXTERNAL;
    }

    public boolean hasAttachment() {
        return this != NONE;
    }

    public boolean isViewableInBrowser() {
        return this == IMAGE || this == VIDEO || this == EXTERNAL;
    }

}
