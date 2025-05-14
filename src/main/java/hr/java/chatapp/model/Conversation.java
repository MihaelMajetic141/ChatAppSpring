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
import org.springframework.data.mongodb.core.mapping.Field;

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

    private String description = "";

    @Field("image_file_id")
    private String imageFileId;

    @Field("is_direct_message")
    @NotNull
    private boolean isDirectMessage;

    @Field("invite_link")
    @Indexed(unique = true)
    private String inviteLink;

    @Field("admin_ids")
    @NotEmpty
    private Set<String> adminIds = new HashSet<>();

    @Field("member_ids")
    @NotEmpty
    private Set<String> memberIds = new HashSet<>();

    private Date createdAt;
}