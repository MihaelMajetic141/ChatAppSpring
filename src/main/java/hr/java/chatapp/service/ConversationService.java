package hr.java.chatapp.service;


import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.model.Message;
import hr.java.chatapp.model.UserInfo;
import hr.java.chatapp.model.dto.ConversationDTO;
import hr.java.chatapp.repository.ConversationRepository;
import hr.java.chatapp.repository.MessageRepository;
import hr.java.chatapp.repository.UserInfoRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public Conversation getById(String conversationId, String currentUserId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        Optional<UserInfo> currentUserOpt = userInfoRepository.findById(currentUserId);
        if (conversationOpt.isEmpty() || currentUserOpt.isEmpty()) {
            return null;
        }
        Conversation conversation = conversationOpt.get();
        if (conversation.isDirectMessage()) {
            String otherUserId = conversation.getMemberIds().stream()
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Invalid DM: missing other user"));
            Optional<UserInfo> otherUserOpt = userInfoRepository.findById(otherUserId);
            if (otherUserOpt.isEmpty()) {
                return null;
            }
            UserInfo otherUser = otherUserOpt.get();
            conversation.setImageFileId(otherUser.getImageFileId());
        }
        return conversation;
    }

    public ConversationDTO getConversationDto(String conversationId, String currentUserId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(conversationId);
        if (conversationOptional.isEmpty())
            return null;
        Conversation conversation = conversationOptional.get();

        Optional<UserInfo> userInfoOpt;
        String displayName;
        String displayImageFileId;
        if (conversation.isDirectMessage()) {
            Set<String> memberIds = conversation.getMemberIds();
            if (!memberIds.contains(currentUserId)) {
                throw new IllegalStateException("Current user ID is not in member list");
            }
            if (memberIds.size() != 2) {
                throw new IllegalStateException("DM must have exactly 2 members");
            }
            String otherUserId = memberIds.stream()
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Invalid DM: missing other user"));
            userInfoOpt = userInfoRepository.findById(otherUserId);
            if (userInfoOpt.isPresent()) {
                displayName = userInfoOpt.get().getUsername();
                displayImageFileId = userInfoOpt.get().getImageFileId();
            } else {
                displayName = "Unknown User";
                displayImageFileId = "";
            }
        }
        else {
            displayName = conversation.getName();
            displayImageFileId = conversation.getImageFileId();
        }
        Message lastMessage = messageRepository.findMessagesByConversationId(conversationId).getLast();
        return ConversationDTO.builder()
                .id(conversationId)
                .name(displayName)
                .lastMessage(lastMessage.getContent())
                .imageFileId(displayImageFileId)
                .build();
    }

    public List<ConversationDTO> getAllConversationsDtoByUserId(String userId) {
        Optional<List<Conversation>> conversationsOpt = conversationRepository.findByMemberIdsContains(userId);
        if (conversationsOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<Conversation> conversations = conversationsOpt.get();
        List<ConversationDTO> conversationDTOList = new ArrayList<>();
        conversations.forEach(
                conversation -> {
                ConversationDTO conversationDTO = getConversationDto(conversation.getId(), userId);
                conversationDTOList.add(conversationDTO);
            }
        );
        return conversationDTOList;
    }

    public Optional<Conversation> getDirectConversationByUserIds(
            @NotNull String userId1,
            @NotNull String userId2
    ) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot create a DM with the same user");
        }
        return conversationRepository
                .findDirectMessageConversation(userId1, userId2);
    }

    public Optional<Conversation> getGroupConversationById(String id) {
        return conversationRepository.findConversationByIdAndDirectMessageFalse(id);
    }

    public Conversation saveNewGroup(Conversation conversation) {
        Conversation group = Conversation.builder()
                .name(conversation.getName())
                .description(conversation.getDescription())
                .imageFileId(conversation.getImageFileId())
                .isDirectMessage(false)
                .inviteLink(generateUniqueInviteLink())
                .adminIds(conversation.getAdminIds())
                .memberIds(conversation.getMemberIds())
                .createdAt(conversation.getCreatedAt())
                .build();
        return conversationRepository.save(group);
    }

    public Conversation saveNewDirectConversation(
            String senderId,
            String receiverId
    ) {
        Optional<Conversation> existingConversation = getDirectConversationByUserIds(senderId, receiverId);
        if (existingConversation.isPresent()) { return existingConversation.get(); }
        Set<String> memberIds = new HashSet<>();
        memberIds.add(senderId);
        memberIds.add(receiverId);
        Conversation newConversation = Conversation.builder()
                .isDirectMessage(true)
                .memberIds(memberIds)
                .createdAt(Date.from(Instant.now()))
                .build();
        return conversationRepository.save(newConversation);
    }

    private String generateUniqueInviteLink() {
        return UUID.randomUUID().toString(); // Simple UUID-based invite link
    }
}
