package hr.java.chatapp.controller;

import hr.java.chatapp.model.dto.ContactDTO;
import hr.java.chatapp.service.ContactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
public class ContactsController {

    @Autowired
    private ContactsService contactsService;

    @GetMapping("/get")
    public ResponseEntity<List<ContactDTO>> getUserContacts(
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(contactsService.getContacts(userId));
    }

    // ToDo: Might become unnecessary
    @GetMapping("/get_all")
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("Authorization") String token
    ) {
        try {
            return ResponseEntity.ok(contactsService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getContactByEmail(
            // @RequestHeader("Authorization") String token,
            @PathVariable String email
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

    @GetMapping("/id/{userId}")
    public ResponseEntity<?> getContactById(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(contactsService.getByUserId(userId));
    }

    @PostMapping("/new/{contactId}")
    public ResponseEntity<ContactDTO> newContact(
            @PathVariable String contactId,
            @RequestParam String currentUserId
    ) {
        return ResponseEntity.ok(contactsService.addNewContact(contactId, currentUserId));
    }

    @PostMapping("/new")
    public ResponseEntity<List<String>> newContacts(
            @RequestParam String currentUserId,
            @RequestBody List<String> contactIdList
    ) {
        return ResponseEntity.ok(contactsService.addNewContacts(contactIdList, currentUserId));
    }



}