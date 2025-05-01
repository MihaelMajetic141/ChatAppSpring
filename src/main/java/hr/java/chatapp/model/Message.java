package hr.java.chatapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
@CompoundIndex(def = "{'conversationId': 1, 'timestamp': -1}")
public class Message {
    @Id
    private String id;
    @NotNull
    @NotBlank
    private String senderId;
    @NotNull
    @NotBlank
    private String conversationId;
    private String content; // Text content
        private String mediaFileId; // GridFS file ID for media
    private String mediaType; // MIME type, e.g., "image/jpeg"
    private String replyTo;
    private Date timestamp; // Long or Date?
    private Map<String, Integer> reactions;
    // private String isDeleted;
    // private Date editedAt;
}