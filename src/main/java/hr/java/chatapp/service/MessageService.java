package hr.java.chatapp.service;

import hr.java.chatapp.model.ChatMessage;
import hr.java.chatapp.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private final GridFsTemplate gridFsTemplate;

    // ToDo: Maybe create class MessageContent for different types of messages.
    public ChatMessage saveMessage(
            String senderId,
            String username,
            String conversationId,
            String content,
            String replyTo
    ) {
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .username(username)
                .conversationId(conversationId)
                .content(content)
                .replyTo(replyTo)
                .timestamp(Date.from(Instant.now()))
                .reactions(new HashMap<>())
                .build();
        return messageRepository.save(chatMessage);
    }

    public List<ChatMessage> getAllMessages(String conversationId) {
        return messageRepository.findMessagesByConversationId(conversationId);
    }

    public void addReaction(String messageId, String emoji) {
        ChatMessage chatMessage = messageRepository.findById(messageId).orElseThrow();
        chatMessage.getReactions().merge(emoji, 1, Integer::sum);
        messageRepository.save(chatMessage);
    }

    public ChatMessage sendMediaMessage(
            String senderId,
            String receiverId,
            String content,
            MultipartFile media
    ) throws Exception {

        if ((content == null || content.isBlank()) && media == null) {
            throw new IllegalArgumentException("Message must have content or media");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .conversationId(receiverId)
                .content(content)
                .timestamp(Date.from(Instant.now()))
                .build();

        if (media != null) {
            String mediaType = media.getContentType();
            String fileId = gridFsTemplate.store(
                    media.getInputStream(), media.getOriginalFilename(), mediaType
            ).toString();
            chatMessage.setMediaFileId(fileId);
            chatMessage.setMediaType(mediaType);
        }

        return messageRepository.save(chatMessage);
    }

}
