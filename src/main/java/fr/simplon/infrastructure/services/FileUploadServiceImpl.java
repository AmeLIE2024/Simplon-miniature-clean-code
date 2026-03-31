package fr.simplon.infrastructure.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import fr.simplon.domain.gateway.FileUploadService;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.VideoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class FileUploadServiceImpl implements FileUploadService {
    private ImageExtension imageExtension;
    private VideoExtension videoExtension;

    public FileUploadServiceImpl() {
    }

    @Override
    private String savedUploadFile(Part filePart, HttpServletRequest req) {

        String originalName = filePart.getSubmittedFileName();

        if (originalName == null || originalName.isBlank()) {
            return null;
        }

        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!imageExtension.equals(extension) && !videoExtension.equals(extension)) {
            return null;
        }

        String safeName = UUID.randomUUID().toString() + "." + extension;
        String uploadDir = req.getServletContext().getRealPath("/uploads");
        File dir = new File(uploadDir);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Impossible de créer le dossier d'upload : " + uploadDir);
        }

        filePart.write(uploadDir + File.separator + safeName);

        System.out.println("Fichier sauvegardé : " + uploadDir + File.separator + safeName);
        System.out.println("URL generee : " + req.getContextPath() + "/uploads/" + safeName);
        String url = req.getContextPath() + "/uploads/" + safeName;
        return url;
    }
}
