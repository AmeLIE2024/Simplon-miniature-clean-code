package fr.simplon.domain.gateway.services;

import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {

    String saveFile(String originalName, InputStream inputStream) throws IOException;
}
