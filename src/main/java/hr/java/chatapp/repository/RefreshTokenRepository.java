package hr.java.chatapp.repository;

import hr.java.chatapp.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> deleteRefreshTokenByToken(String refreshToken);
    Optional<RefreshToken> findByUserId(String userId);
}