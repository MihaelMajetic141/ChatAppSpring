package hr.java.chatapp.repository;

import hr.java.chatapp.model.MediaMetadata;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaRepository extends MongoRepository<MediaMetadata, ObjectId> {
}
