package com.artsolo.phonecontacts.contact;

import com.artsolo.phonecontacts.contact.dto.AddContactRequest;
import com.artsolo.phonecontacts.contact.dto.ContactResponse;
import com.artsolo.phonecontacts.responses.DataResponse;
import com.artsolo.phonecontacts.responses.MessageResponse;
import com.artsolo.phonecontacts.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/create")
    public ResponseEntity<DataResponse<ContactResponse>> createContact(@RequestBody @Valid AddContactRequest addContactRequest, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.createNewContact(addContactRequest, user);
        return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(), contactService.getContactResponseFromContact(contact)));
    }

    @PutMapping("/rename")
    public ResponseEntity<?> renameContact(@RequestParam("id") Long id, @RequestParam("name") String name, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(id);
        if (contactService.isUserContactOwner(contact, user)) {
            Contact svdContact = contactService.renameContact(contact, name);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    contactService.getContactResponseFromContact(svdContact)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(), "You are not the owner of the contact to perform this action."));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<MessageResponse> deleteContact(@RequestParam("id") Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(id);
        if (contactService.isUserContactOwner(contact, user)) {
            contactService.deleteContact(contact);
            return ResponseEntity.ok().body(new MessageResponse(true, HttpStatus.OK.value(),
                    "Contact was deleted successfully."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getContactById(@RequestParam("id") Long id, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(id);
        if (contactService.isUserContactOwner(contact, user)) {
            contactService.deleteContact(contact);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    contactService.getContactResponseFromContact(contact)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @GetMapping("/list")
    public ResponseEntity<DataResponse<List<ContactResponse>>> getContactList(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                contactService.getUserContacts(user)));
    }
}
