package hr.java.chatapp.controller;

import hr.java.chatapp.model.dto.ContactDTO;
import hr.java.chatapp.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactsController {

    @Autowired
    private ContactsService contactsService;

    @GetMapping("/get")
    public ResponseEntity<List<ContactDTO>> getContactDTO(
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(contactsService.getContacts(userId));
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<ContactDTO>> getAllUsers() {
        return ResponseEntity.ok(contactsService.getAll());
    }

    @GetMapping("get_by_email")
    public ResponseEntity<?> getContactByEmail(
            // @RequestHeader("Authorization") String token,
            @RequestParam String email
    ) {
        try {
//            if (!userInfoService.authenticateJwtHeader(username, token)) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Optional.empty());
//            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(contactsService.getByEmail(email));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/new/{contactId}")
    public ResponseEntity<ContactDTO> newContact(
            @PathVariable String contactId,
            @RequestParam String currentUserId
    ) {
        return ResponseEntity.ok(contactsService.addNewContact(contactId, currentUserId));
    }



}