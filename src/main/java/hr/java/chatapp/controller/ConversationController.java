package hr.java.chatapp.controller;

import hr.java.chatapp.model.Conversation;
import hr.java.chatapp.model.dto.ConversationDTO;
import hr.java.chatapp.service.ConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    @Autowired
    private GridFsTemplate gridFsTemplate;

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

    @GetMapping("/getImage")
    public ResponseEntity<GridFsResource> getConversationImage(
            @RequestParam String conversationId,
            @RequestParam String userId
    ) {
        Conversation conversation = conversationService
                .getById(conversationId, userId);
        String imageId = conversation.getImageFileId();

        GridFsResource resource = gridFsTemplate.getResource(imageId);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .body(resource);
    }

    @PostMapping("/create_group")
    public ResponseEntity<Conversation> createGroup(
            @Valid @RequestBody Conversation conversation
    ) {
        return ResponseEntity.ok(
                conversationService.saveNewGroup(conversation)
        );
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


    // ToDo: Read status here

    // Additional endpoints: get group by ID, update group, etc.
}