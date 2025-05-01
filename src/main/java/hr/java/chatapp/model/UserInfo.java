package hr.java.chatapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@Document(collection = "users")
@CompoundIndexes({
        @CompoundIndex(name = "username_index", def = "{'username': 1}", unique = true),
        @CompoundIndex(name = "email_index", def = "{'email': 1}", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    @Id
    private String id;
    @NotBlank
    @Size(max = 20)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    @Size(max = 120)
    private String password;
    @Field("profile_picture")
    private String pictureFileId;
    private String status = "";
    private Set<String> roles = new HashSet<>();
    private Date lastOnline;
    private boolean isOnline;
    private List<String> contactIds = new ArrayList<>();
}



//@Entity
//@Table(name = "users",
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = "username"),
//                @UniqueConstraint(columnNames = "email")
//        })
//public class UserInfo {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotBlank
//    @Size(max = 20)
//    private String username;
//
//    @NotBlank
//    @Size(max = 50)
//    @Email
//    private String email;
//
//    @Size(max = 120)
//    private String password;
//
//    @Column(name = "profile_picture")
//    private String profilePicture;
//
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "user_roles",
//            joinColumns = @JoinColumn(name = "id_user"),
//            inverseJoinColumns = @JoinColumn(name = "id_role"))
//    private Set<Role> roles = new HashSet<>();
//}