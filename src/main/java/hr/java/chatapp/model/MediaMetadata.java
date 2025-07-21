package hr.java.chatapp.model;


import lombok.Builder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("media_metadata")
@Builder
public record MediaMetadata(
        @Id String id,
        String originalName,
        String storedName,
        String mimeType,
        String ownerId,
        long size,
        Instant createdAt
) {}
