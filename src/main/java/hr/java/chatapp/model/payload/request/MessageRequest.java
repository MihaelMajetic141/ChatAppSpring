package hr.java.chatapp.model.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {
    @NotBlank
    private String message;
    @NotNull
    private String senderId;
    @NotNull
    private String conversationId;
    private String replyTo;
    // private String mediaFileId; // GridFS file ID for media
    // private String mediaType; // MIME type, e.g., "image/jpeg"
}
