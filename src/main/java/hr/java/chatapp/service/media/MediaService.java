package hr.java.chatapp.service.media;


import hr.java.chatapp.model.MediaMetadata;
import hr.java.chatapp.repository.MediaRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
@AllArgsConstructor
public class MediaService {

    private final MediaStorageProperties properties;
    private final MediaRepository metadataRepository;
    private final LocalMediaStorageService storageService;

    public MediaMetadata uploadMedia(MultipartFile file, String ownerId) throws IOException {
        validateImage(file);
        String storagePath;
        try(InputStream inputStream = file.getInputStream()) {
            storagePath = storageService.storeFile(inputStream, file.getOriginalFilename());
        }

        MediaMetadata metadata = new MediaMetadata(
                ObjectId.get(),
                file.getOriginalFilename(),
                storagePath,
                file.getContentType(),
                ownerId,
                file.getSize(),
                Instant.now()
        );

        return metadataRepository.save(metadata);
    }

    public Resource getImageResource(String imageId) throws IOException {
        MediaMetadata mediaMetadata = getImageMetadata(imageId);
        return storageService.getFileResource(mediaMetadata.storedName());
    }

    public MediaMetadata getImageMetadata(String imageId) throws IOException {
        ObjectId objectId = new ObjectId(imageId);
        return metadataRepository.findById(objectId).orElseThrow(
                () -> new FileNotFoundException("File not found")
        );
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("File is empty");

        String mimeType = file.getContentType();
        if (mimeType == null || !properties.allowedMimeTypes().contains(mimeType) || mimeType.isEmpty())
            throw new IllegalArgumentException("Mime type is empty");
    }
}
