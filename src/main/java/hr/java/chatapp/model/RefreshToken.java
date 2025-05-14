package hr.java.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "refresh_token")
public class RefreshToken {
    @Id
    private String id;
    @Field("token")
    private String token;
    @Field("expiry_date")
    private Instant expiryDate;
    @Field("user_id")
    private String userId;
}


//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Entity
//@Document(name = "refresh_token")
//public class RefreshToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//    @Column(name = "token")
//    private String token;
//    @Column(name = "expiry_date")
//    private Instant expiryDate;
//    @OneToOne
//    @JoinColumn(referencedColumnName = "id")
//    private UserInfo user;
//}