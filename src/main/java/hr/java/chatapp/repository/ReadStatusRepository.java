package hr.java.chatapp.repository;


import hr.java.chatapp.model.ReadStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends MongoRepository<ReadStatus, String> {

}
