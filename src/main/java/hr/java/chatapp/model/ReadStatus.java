package hr.java.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "read_statuses")
@CompoundIndexes({
        @CompoundIndex(name = "user_chatgroup_index", def = "{'userId': 1, 'conversationId': 1}", unique = true),
        @CompoundIndex(name = "chatgroup_lastread_index", def = "{'conversationId': 1, 'lastReadMessageId': 1}")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadStatus {
    @Id
    private String id;
    private String userId;
    private String conversationId;
    private String lastReadMessageId;
    private Date lastUpdated;
}