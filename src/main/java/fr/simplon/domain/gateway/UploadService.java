package fr.simplon.domain.gateway;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/uploads/*")
public class UploadService extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getPathInfo();

        if (fileName == null || fileName.equals("/")) {
            resp.sendError(404);
            return;
        }

        String uploadDir = getServletContext().getRealPath("/uploads");
        File file = new File(uploadDir + fileName);

        if (!file.getCanonicalPath().startsWith(new File(uploadDir).getCanonicalPath())) {
            resp.sendError(403);
            return;
        }

        if (!file.exists() || !file.isFile()) {
            resp.sendError(404);
            return;
        }

        String mimeType = Files.probeContentType(file.toPath());
        resp.setContentType(mimeType != null ? mimeType : "application/octet-stream");

        try (InputStream in = new FileInputStream(file)) {
            in.transferTo(resp.getOutputStream());
        }
    }
}
