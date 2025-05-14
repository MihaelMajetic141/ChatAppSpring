package hr.java.chatapp.model.payload.response;

import hr.java.chatapp.model.dto.UserInfoDTO;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuthResponse {
    private JwtResponse jwtResponse;
    private UserInfoDTO userInfo;
}
