package hr.java.chatapp.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("api/media")
public class MediaController {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<GridFsResource> getMedia(@PathVariable String fileId) {
        GridFsResource resource = gridFsTemplate.getResource(fileId);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ObjectId fileId = gridFsTemplate.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );
            return ResponseEntity.ok(fileId.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload media: " + e.getMessage());
        }
    }
}
