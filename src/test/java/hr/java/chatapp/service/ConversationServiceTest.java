package hr.java.chatapp.service;


import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.repository.ConversationRepository;
import org.apache.commons.logging.Log;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ConversationServiceTest {

    @MockitoBean
    private ConversationRepository conversationRepository;
    @Autowired
    private ConversationService conversationService;

    @Test
    void saveNewGroup_CreatesValidConversation() {
        // Given
        Conversation group = Conversation.builder()
                .id(ObjectId.get().toString())
                .name("groupTestName")
                .description("")
                .imageFileId("mockImageId")
                .isDirectMessage(false)
                .inviteLink("randomLink")
                .adminIds(List.of("id1"))
                .memberIds(List.of("id1", "id2", "id3"))
                .createdAt(Date.from(Instant.now()))
                .build();

        // When
        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(group);
        Conversation result = conversationService.saveNewGroup(group);
        System.out.println(result);

        // Then
        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(captor.capture());
        Conversation savedConversation = captor.getValue();
        System.out.println("Saved conversation = " + savedConversation);


        assertThat(savedConversation.getName()).isEqualTo(group.getName());
        assertThat(savedConversation.getDescription()).isEqualTo(group.getDescription());
        assertThat(savedConversation.getImageFileId()).isEqualTo(group.getImageFileId());
        assertThat(savedConversation.isDirectMessage()).isEqualTo(group.isDirectMessage());
        assertThat(savedConversation.getAdminIds()).isEqualTo(group.getAdminIds());
        assertThat(savedConversation.getMemberIds()).isEqualTo(group.getMemberIds());
        assertThat(savedConversation.getCreatedAt()).isEqualTo(group.getCreatedAt());
    }

}
