package hr.java.chatapp.controller;

import hr.java.chatapp.model.Message;
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
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping("/get")
    public ResponseEntity<List<Message>> getConversation(
            @RequestParam String conversationId
    ) {
        List<Message> messages = messageService.getAllMessages(conversationId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/media/{fileId}")
    public ResponseEntity<GridFsResource> getMedia(@PathVariable String fileId) {
        //ToDo: Secure this by checking if currentUser is in conversation where media is located.

        GridFsResource resource = gridFsTemplate.getResource(fileId);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .body(resource);
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