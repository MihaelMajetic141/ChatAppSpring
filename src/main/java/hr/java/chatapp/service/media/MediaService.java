package hr.java.chatapp.service.media;


import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.model.MediaMetadata;
import hr.java.chatapp.repository.ConversationRepository;
import hr.java.chatapp.repository.MediaRepository;
import hr.java.chatapp.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MediaService {

    private final MediaStorageProperties properties;
    private final MediaRepository metadataRepository;
    private final LocalMediaStorageService storageService;
    private final ConversationRepository conversationRepository;
    private final UserInfoRepository userInfoRepository;

    public MediaMetadata uploadMedia(MultipartFile file, String ownerId) throws IOException {
        validateMedia(file);
        String storagePath;
        try(InputStream inputStream = file.getInputStream()) {
            storagePath = storageService.storeFile(inputStream, file.getOriginalFilename());
        }

        MediaMetadata metadata = MediaMetadata.builder()
                .id(ObjectId.get().toString())
                .originalName(file.getOriginalFilename())
                .storedName(storagePath)
                .mimeType(file.getContentType())
                .ownerId(ownerId)
                .size(file.getSize())
                .size(file.getSize())
                .build();

        return metadataRepository.save(metadata);
    }

    public Resource getMediaResource(String imageId) throws IOException {
        MediaMetadata mediaMetadata = getMediaMetadata(imageId);
        // validateMediaAccess();
        return storageService.getFileResource(mediaMetadata.storedName());
    }

    public MediaMetadata getMediaMetadata(String imageId) throws IOException {
        ObjectId objectId = new ObjectId(imageId);
        return metadataRepository.findById(objectId).orElseThrow(
                () -> new FileNotFoundException("File not found")
        );
    }

    private void validateMedia(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalArgumentException("File is empty");

        String mimeType = file.getContentType();
        if (mimeType == null || !properties.allowedMimeTypes().contains(mimeType) || mimeType.isEmpty())
            throw new IllegalArgumentException("Mime type is empty");
    }

    private void validateMediaAccess(MediaMetadata mediaMetadata, String currentUserId) {
        Optional<Conversation> conversation = conversationRepository.findById(mediaMetadata.ownerId());
        conversation.ifPresent(value -> value.getMemberIds().stream()
                .filter(id -> id.equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new SecurityException("Unauthorized")));

    }
}
