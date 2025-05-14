package hr.java.chatapp.service;

import hr.java.chatapp.model.Message;
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
    public Message saveMessage(
            String senderId,
            String conversationId,
            String content,
            String replyTo
    ) {
        Message message = Message.builder()
                .senderId(senderId)
                .conversationId(conversationId)
                .content(content)
                .replyTo(replyTo)
                .timestamp(Date.from(Instant.now()))
                .reactions(new HashMap<>())
                .build();
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages(String conversationId) {
        return messageRepository.findMessagesByConversationId(conversationId);
    }

    public void addReaction(String messageId, String emoji) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.getReactions().merge(emoji, 1, Integer::sum);
        messageRepository.save(message);
    }

    public Message sendMediaMessage(
            String senderId,
            String receiverId,
            String content,
            MultipartFile media
    ) throws Exception {

        if ((content == null || content.isBlank()) && media == null) {
            throw new IllegalArgumentException("Message must have content or media");
        }

        Message message = Message.builder()
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
            message.setMediaFileId(fileId);
            message.setMediaType(mediaType);
        }

        return messageRepository.save(message);
    }

}
