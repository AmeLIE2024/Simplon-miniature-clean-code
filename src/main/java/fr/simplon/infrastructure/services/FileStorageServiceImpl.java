package fr.simplon.infrastructure.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import fr.simplon.domain.gateway.services.FileStorageService;

public class FileStorageServiceImpl implements FileStorageService {

    private final String uploadDir;
    private final String contextPath;

    public FileStorageServiceImpl(String uploadDir, String contextPath) {
        this.uploadDir = uploadDir;
        this.contextPath = contextPath;
    }

    @Override
    public String saveFile(String originalName, InputStream inputStream) throws IOException {

        if (originalName == null || originalName.isBlank()) {
            return null;
        }

        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase()
                : "";

        String safeName = UUID.randomUUID() + "." + extension;

        File dir = new File(uploadDir);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Impossible de créer le dossier : " + uploadDir);
        }

        Files.copy(inputStream,
                new File(dir, safeName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return contextPath + "/uploads/" + safeName;
    }
}