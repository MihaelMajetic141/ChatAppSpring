package hr.java.chatapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotBlank;
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

    private String name;

    private String description;

    @Field("image_file_id")
    private String imageFileId;

    @Field("is_direct_message")
    private boolean isDirectMessage;

    @Field("invite_link")
    @Indexed(name = "invite_link_unique", unique = true)
    private String inviteLink;

    @Field("admin_ids")
    private List<String> adminIds = new ArrayList<>();

    @Field("member_ids")
    private List<String> memberIds = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    @Field("created_at")
    private Date createdAt;
}