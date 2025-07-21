package hr.java.chatapp.controller;

import hr.java.chatapp.model.MediaMetadata;
import hr.java.chatapp.service.media.MediaService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@AllArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file
    ) {
        String ownerId = "ownerId";
        try {
            MediaMetadata metadata = mediaService.uploadMedia(file, ownerId);
            return ResponseEntity.ok(
                    MediaMetadata.builder()
                            .id(metadata.id())
                            .originalName(metadata.originalName())
                            .size(metadata.size())
                            .build()
            );

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadMedia(
            @PathVariable String fileId
//            @RequestParam String userId,
//            @RequestParam (required = false) String conversationId
    ) {
//        if (!conversationId.contains(userId))
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            MediaMetadata metadata = mediaService.getMediaMetadata(fileId);
            Resource resource = mediaService.getMediaResource(fileId);

            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + metadata.originalName() + "\""
                    )
                    .contentType(MediaType.parseMediaType(metadata.mimeType()))
                    .contentLength(metadata.size())
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
