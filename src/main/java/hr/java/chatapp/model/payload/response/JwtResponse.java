package hr.java.chatapp.model.payload.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
}
