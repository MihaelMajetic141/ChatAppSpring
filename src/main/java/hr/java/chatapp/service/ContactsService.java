package hr.java.chatapp.service;


import hr.java.chatapp.model.UserInfo;
import hr.java.chatapp.model.dto.ContactDTO;
import hr.java.chatapp.repository.UserInfoRepository;
import hr.java.chatapp.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactsService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private JwtService jwtService;

    public ContactDTO addNewContact(String newContactId, String currentUserId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(currentUserId);
        Optional<UserInfo> contactUser = userInfoRepository.findById(newContactId);

        if (currentUser.isPresent() && contactUser.isPresent()) {
            List<String> contactList = currentUser.get().getContactIds();
            contactList.add(newContactId);
            currentUser.get().setContactIds(contactList);
            userInfoRepository.save(currentUser.get());

            return userInfoToContactDTO(contactUser.get());
        }
        else return null;
    }

    public List<String> addNewContacts(List<String> contactIdList, String currentUserId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(currentUserId);
        if (currentUser.isEmpty()) return null;

        List<String> currentUserContacts = currentUser.get().getContactIds();
        contactIdList.forEach(contactId -> {
            Optional<UserInfo> contactUser = userInfoRepository.findById(contactId);
            if (contactUser.isPresent()) {
                currentUserContacts.add(contactId);
                userInfoRepository.save(currentUser.get());
            }
        });

        return currentUserContacts;
    }

    public List<ContactDTO> getContacts(String currentUserId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(currentUserId);
        if (currentUser.isEmpty()) return null;

        List<String> contactIdList = currentUser.get().getContactIds();

        // ToDo: Check if currentUser's contacts are valid.
        List<UserInfo> contactList = contactIdList.stream()
                .map(id -> userInfoRepository.findById(id).orElse(null))
                .toList();

        return contactList.stream()
            .map(this::userInfoToContactDTO).toList();
    }

    public List<ContactDTO> getAll() {
        return userInfoRepository.findAll().stream()
                .map(this::userInfoToContactDTO)
                .toList();
    }

    public ContactDTO getByEmail(String email) {
        Optional<UserInfo> currentUser = userInfoRepository.findByEmail(email);
        return currentUser.map(this::userInfoToContactDTO).orElse(null);
    }

    public ContactDTO getByUserId(String userId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(userId);
        return currentUser.map(this::userInfoToContactDTO).orElse(null);
    }

    private ContactDTO userInfoToContactDTO(UserInfo userInfo) {
        return ContactDTO.builder()
                .userInfoId(userInfo.getId())
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .imageFileId(userInfo.getImageFileId())
                .status(userInfo.getStatus())
                .build();
    }

    public boolean authenticateJwtHeader(String username, String token) {
        String jwt = token.substring(7);
        String email = jwtService.extractEmail(jwt);
        Optional<UserInfo> user = userInfoRepository.findByEmail(email);
        return user.isPresent() && user.get().getUsername().equals(username);
    }
}
