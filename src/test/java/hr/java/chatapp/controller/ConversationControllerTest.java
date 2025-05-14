package hr.java.chatapp.controller;

import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.security.JwtAuthFilter;
import hr.java.chatapp.security.JwtService;
import hr.java.chatapp.security.SecurityConfiguration;
import hr.java.chatapp.service.ConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(ConversationController.class)
// @ContextConfiguration(classes={SimpleTestConfig.class})
// @Import(SecurityConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ConversationService conversationService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    public void testCreateGroup() throws Exception {
        assert conversationService != null : "conversationService should not be null";

        // Arrange
        String name = "Test Group";
        String description = "This is a test group";
        String imageFileId = "image123";
        String creatorId = "user1";
        Set<String> memberIds = Set.of("user1", "user2", "user3");

        Conversation mockConversation = Conversation.builder()
                .id("conv1")
                .name(name)
                .description(description)
                .imageFileId(imageFileId)
                .isDirectMessage(false)
                .adminIds(Set.of(creatorId))
                .memberIds(memberIds)
                .inviteLink("uniqueLink123")
                .build();

//        when(conversationService.saveNewGroup(name, description, imageFileId, creatorId, memberIds))
//                .thenReturn(mockConversation);

        // Act & Assert
        mockMvc.perform(post("/api/conversations/create_group")
                        .param("name", name)
                        .param("description", description)
                        .param("imageFileId", imageFileId)
                        .param("creatorId", creatorId)
                        .param("memberIds", "user1", "user2", "user3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("conv1"))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.imageFileId").value(imageFileId))
                .andExpect(jsonPath("$.isDirectMessage").value(false))
                .andExpect(jsonPath("$.adminIds", hasSize(1)))
                .andExpect(jsonPath("$.adminIds", hasItem(creatorId)))
                .andExpect(jsonPath("$.memberIds", hasSize(3)))
                .andExpect(jsonPath("$.memberIds", hasItem("user1")))
                .andExpect(jsonPath("$.memberIds", hasItem("user2")))
                .andExpect(jsonPath("$.memberIds", hasItem("user3")))
                .andExpect(jsonPath("$.inviteLink").value("uniqueLink123"));

        // verify(conversationService).saveNewGroup(name, description, imageFileId, creatorId, memberIds);
    }

    @Test
    void createConversation_ValidRequest_ReturnsCreatedConversation() throws Exception {
        // Mock service response
        Conversation mockConversation = Conversation.builder()
                .name("Test Group")
                .description("Test Description")
                .build();

//        when(conversationService.saveNewGroup(
//                anyString(), anyString(), anyString(), anyString(), anySet()
//        )).thenReturn(mockConversation);

        // Execute request and verify
        mockMvc.perform(post("/api/conversations/create_group")
                        .param("name", "Test Group")
                        .param("description", "Test Description")
                        .param("imageFileId", "img123")
                        .param("creatorId", "user1")
                        .param("memberIds", "user2", "user3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Group"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        // Verify service interaction
//        verify(conversationService).saveNewGroup(
//                "Test Group", "Test Description", "img123", "user1", Set.of("user2", "user3")
//        );
    }

    @Test
    public void testCreateDirectMessage() throws Exception {
        // Arrange
        String user1 = "userA";
        String user2 = "userB";

        Conversation mockConversation = Conversation.builder()
                .id("dm1")
                .isDirectMessage(true)
                .memberIds(Set.of(user1, user2))
                .build();

        when(conversationService.saveNewDirectConversation(user1, user2))
                .thenReturn(mockConversation);

        // Act & Assert
        mockMvc.perform(post("/api/conversations/create_dm")
                        .param("user1", user1)
                        .param("user2", user2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("dm1"))
                .andExpect(jsonPath("$.isDirectMessage").value(true))
                .andExpect(jsonPath("$.memberIds", hasSize(2)))
                .andExpect(jsonPath("$.memberIds", hasItem(user1)))
                .andExpect(jsonPath("$.memberIds", hasItem(user2)));

        verify(conversationService).saveNewDirectConversation(user1, user2);
    }
}
