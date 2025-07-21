package hr.java.chatapp.controller;

import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.model.dto.ConversationDTO;
import hr.java.chatapp.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@AllArgsConstructor
public class ConversationController {

    @Autowired
    private final ConversationService conversationService;

    @GetMapping("/get/{conversationId}/{currentUserId}")
    public ResponseEntity<Conversation> getConversation(
            @PathVariable String conversationId,
            @PathVariable String currentUserId
    ) {
        return ResponseEntity.ok(
                conversationService.getById(conversationId, currentUserId)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ConversationDTO>> getAllConversationDTOsByUserId(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(
                conversationService.getAllConversationsDtoByUserId(userId)
        );
    }

    @PostMapping("/create_group")
    public ResponseEntity<Conversation> createGroup(
            @RequestBody Conversation conversation
    ) {
        Conversation newConversation = conversationService.saveNewGroup(conversation);
        System.out.println(newConversation);
        return ResponseEntity.ok(newConversation);
    }

    @PostMapping("/create_dm")
    public ResponseEntity<Conversation> createDM(
        @RequestParam String senderId,
        @RequestParam String receiverId
    ) {
        return ResponseEntity.ok(
                conversationService.saveNewDirectConversation(senderId, receiverId)
        );
    }


    // ToDo: Read status
}