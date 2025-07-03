package hr.java.chatapp.repository;

import hr.java.chatapp.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findMessagesByConversationId(String conversationId);
}
