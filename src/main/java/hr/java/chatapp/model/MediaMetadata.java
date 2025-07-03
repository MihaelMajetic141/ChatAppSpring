package hr.java.chatapp.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("media_metadata")
public record MediaMetadata(
        @Id ObjectId id,
        String originalName,
        String storedName,
        String mimeType,
        String ownerId,
        long size,
        Instant createdAt
) {}
