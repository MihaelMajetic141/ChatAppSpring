package hr.java.chatapp.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserInfoDTO {
    private String id;
    private String username;
    private String email;
    private String imageFileId;
    private boolean isOnline;
    private Date lastOnline;
    private List<String> contactIds;
}
