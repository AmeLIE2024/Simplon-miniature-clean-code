package fr.simplon.domain.models;

import java.util.Arrays;

public enum AttachmentType {
    LINK,
    IMAGE,
    VIDEO,
    DOCUMENT,
    POST,
    NONE,
    EXTERNAL;

    public static AttachmentType fromExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return NONE;

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        boolean isImage = Arrays.stream(ImageExtension.values()).anyMatch(e -> e.name().equals(extension));
        if (isImage)
            return IMAGE;

        boolean isVideo = Arrays.stream(VideoExtension.values()).anyMatch(e -> e.name().equals(extension));
        if (isVideo)
            return VIDEO;

        return NONE;
    }
}
