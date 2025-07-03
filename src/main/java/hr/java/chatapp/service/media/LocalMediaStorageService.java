package hr.java.chatapp.service.media;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;


@Service
public class LocalMediaStorageService {

    private final Path rootPath;

    public LocalMediaStorageService(MediaStorageProperties properties) {
        this.rootPath = Paths.get(properties.basePath());
    }

    public String storeFile(InputStream inputStream, String originalFilename) throws IOException {
        Path dateDirectory = rootPath.resolve(
                LocalDate.now().getYear()
                        + File.separator +
                        String.format("%02d", LocalDate.now().getMonthValue())
                        + File.separator +
                        String.format("%02d", LocalDate.now().getDayOfMonth())
        );
        Files.createDirectories(dateDirectory);

        String ext = getFileExtension(originalFilename);
        String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path filePath = dateDirectory.resolve(storedName);

        try(OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW)) {
            StreamUtils.copy(inputStream, outputStream);
        }

        return rootPath.relativize(filePath).toString();
    }

    public Resource getFileResource(String storedPath) throws IOException {
        Path filePath = rootPath.resolve(storedPath).normalize().toAbsolutePath();
        Path normalizedRoot = rootPath.normalize().toAbsolutePath();
        if (!filePath.startsWith(normalizedRoot)) {
            throw new SecurityException("Access denied");
        }
        if (Files.notExists(filePath)) {
            throw new FileNotFoundException("File not found");
        }
        return new UrlResource(filePath.toUri());
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot + 1);
    }

}
