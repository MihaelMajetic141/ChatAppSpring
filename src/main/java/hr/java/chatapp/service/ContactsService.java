package hr.java.chatapp.service;


import hr.java.chatapp.model.UserInfo;
import hr.java.chatapp.model.dto.ContactDTO;
import hr.java.chatapp.repository.UserInfoRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContactsService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    public ContactDTO addNewContact(String contactId, String currentUserId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(currentUserId);
        Optional<UserInfo> contactUser = userInfoRepository.findById(contactId);

        if (currentUser.isPresent() && contactUser.isPresent()) {
            List<String> contactList = currentUser.get().getContactIds();
            contactList.add(contactId);
            currentUser.get().setContactIds(contactList);

            return ContactDTO.builder()
                    .userInfoId(currentUser.get().getId())
                    .email(currentUser.get().getEmail())
                    .username(currentUser.get().getUsername())
                    .imageFileId(currentUser.get().getPictureFileId())
                    .status(currentUser.get().getStatus())
                    .build();
        }
        else return null;
    }

    // ToDo: Check if currentUser's contacts are valid. Somewhere else.
    public List<ContactDTO> getContacts(String contactId) {
        Optional<UserInfo> currentUser = userInfoRepository.findById(contactId);
        if (currentUser.isEmpty()) return null;

        List<ContactDTO> contactDTOList = new ArrayList<>();
        List<String> contactIdList = currentUser.get().getContactIds();
        List<UserInfo> contactList = contactIdList.stream()
                .map(id -> userInfoRepository.findById(id).orElse(null)).toList();

        contactList.stream()
            .map(userInfo ->
                ContactDTO.builder()
                    .userInfoId(userInfo.getId())
                    .email(userInfo.getEmail())
                    .username(userInfo.getUsername())
                    .imageFileId(userInfo.getPictureFileId())
                    .status(userInfo.getStatus())
                    .build()
            ).forEach(contactDTOList::add);

        return contactDTOList;
    }

    public List<ContactDTO> getAll() {
        List<ContactDTO> contactDTOList = new ArrayList<>();
        userInfoRepository.findAll().stream()
                .map(userInfo ->
                        ContactDTO.builder()
                                .userInfoId(userInfo.getId())
                                .email(userInfo.getEmail())
                                .username(userInfo.getUsername())
                                .imageFileId(userInfo.getPictureFileId())
                                .status(userInfo.getStatus())
                                .build()
                )
                .forEach(contactDTOList::add);
        return contactDTOList;
    }


}
