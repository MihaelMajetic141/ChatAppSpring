package hr.java.chatapp.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ConversationDTO {
    private String id;
    private String name;
    private String lastMessage;
    private Date lastMessageDate;
    private String imageFileId;
}
