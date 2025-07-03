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

@RestController("/api/media")
@AllArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file
    ) {
        // ToDo: Get from parsed jwt claim.
        String ownerId = "ownerId";

        try {
            MediaMetadata metadata = mediaService.uploadMedia(file, ownerId);

            // ToDo: Create a class
            return ResponseEntity.ok(
                    Map.ofEntries(
                            Map.entry("imageId", metadata.id().toHexString()),
                            Map.entry("originalName", metadata.originalName()),
                            Map.entry("size", metadata.size())
                    )
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
    ) {
        try {
            MediaMetadata metadata = mediaService.getImageMetadata(fileId);
            Resource resource = mediaService.getImageResource(fileId);

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
