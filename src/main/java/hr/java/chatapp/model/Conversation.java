package hr.java.chatapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "conversations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;

    private String imageFileId;

    @NotNull
    private boolean isDirectMessage;

    @Indexed(unique = true)
    private String inviteLink;

    // ToDo: Check if @NotEmpty
    private List<String> adminIds = new ArrayList<>();

    @NotEmpty
    private List<String> memberIds = new ArrayList<>();

    private Date createdAt;
}