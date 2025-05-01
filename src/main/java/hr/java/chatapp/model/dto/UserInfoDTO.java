package hr.java.chatapp.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfoDTO {
    private String username;
    private String email;
    private String pictureFileId;
}
