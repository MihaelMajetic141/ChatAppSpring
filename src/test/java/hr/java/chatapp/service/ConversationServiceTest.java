package hr.java.chatapp.service;


import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.repository.ConversationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
        Set<String> memberIds = Set.of("user2", "user3");
        Conversation expectedConversation = Conversation.builder()
                .adminIds(Set.of("user1"))
                .memberIds(memberIds)
                .build();

        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(expectedConversation);

        // When
//        Conversation result = conversationService.saveNewGroup(
//                "Test Group", "Test Desc", "img123", "user1", memberIds
//        );

        // Then
        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(captor.capture());

        Conversation savedConversation = captor.getValue();
        assertThat(savedConversation.getName()).isEqualTo("Test Group");
        assertThat(savedConversation.getAdminIds()).containsExactly("user1");
        assertThat(savedConversation.getInviteLink()).isNotBlank();
        assertThat(savedConversation.getCreatedAt()).isNotNull();
    }

}
