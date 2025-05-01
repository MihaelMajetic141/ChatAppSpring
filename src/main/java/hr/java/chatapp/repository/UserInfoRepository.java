package hr.java.chatapp.repository;

import hr.java.chatapp.model.UserInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    Optional<UserInfo> findByUsername(String username);
    Optional<UserInfo> findByEmail(String email);
    @NotNull Optional<UserInfo> findById(@NotNull String id);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
