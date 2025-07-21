package hr.java.chatapp.controller;

import hr.java.chatapp.model.ChatMessage;
import hr.java.chatapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/get")
    public ResponseEntity<List<ChatMessage>> getMessagesByConversationId(
            @RequestParam String conversationId
    ) {
        List<ChatMessage> chatMessages = messageService.getAllMessages(conversationId);
        return ResponseEntity.status(HttpStatus.OK).body(chatMessages);
    }

//    @PostMapping("/new_message")
//    public ResponseEntity<Message> saveNewMessage(
//            @Valid @RequestBody MessageRequest messageRequest
//    ) {
//        return ResponseEntity.ok(messageService.saveMessage(
//                messageRequest.getSenderId(),
//                messageRequest.getConversationId(),
//                messageRequest.getMessage(),
//                messageRequest.getReplyTo())
//        );
//    }

//    @GetMapping("/images/{imageId}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
//        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(imageId)));
//        if (gridFSFile != null) {
//            try (GridFSDownloadStream downloadStream = gridFsBucket.openDownloadStream(gridFSFile.getObjectId())) {
//                byte[] imageData = IOUtils.toByteArray(downloadStream);
//                return ResponseEntity.ok()
//                        .contentType(MediaType.IMAGE_JPEG) // Adjust based on image type
//                        .body(imageData);
//            }
//        }
//        return ResponseEntity.notFound().build();
//    }
    // ToDo: Post media method




}