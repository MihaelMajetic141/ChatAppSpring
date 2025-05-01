package hr.java.chatapp.repository;

import hr.java.chatapp.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<List<Conversation>> findByMemberIdsContains(String memberIds);

    @Query("{'isDirectMessage': true, 'memberIds': { '$all': [?0, ?1], '$size': 2 }}")
    Optional<Conversation> findDirectMessageConversation(String userId1, String userId2);

    Optional<Conversation> findConversationByIdAndDirectMessageFalse(String conversationId);

}
