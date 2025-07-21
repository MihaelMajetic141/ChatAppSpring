package hr.java.chatapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
@CompoundIndex(def = "{'conversationId': 1, 'timestamp': -1}")
public class ChatMessage {
    @Id
    private String id;

    @NotNull
    @NotBlank
    @Field("sender_id")
    private String senderId;

    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    @Field("conversation_id")
    private String conversationId;

    @NotBlank
    private String content;

    @Field("media_file_id")
    private String mediaFileId;

    @Field("media_type")
    private String mediaType;

    @Field("reply_to")
    private String replyTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Date timestamp;

    private Map<String, Integer> reactions;

    // private String isDeleted;
    // private Date editedAt;
}