package hr.java.chatapp.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDTO {
    private String userInfoId;
    private String username;
    private String email;
    private String imageFileId;
    private String status;
}
