package com.artsolo.phonecontacts.email;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactService;
import com.artsolo.phonecontacts.responses.DataResponse;
import com.artsolo.phonecontacts.responses.MessageResponse;
import com.artsolo.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact/{contactId}")
public class EmailAddressController {

    private final EmailAddressService emailService;
    private final ContactService contactService;

    @PostMapping("/add/email")
    public ResponseEntity<?> addNewEmailAddressToContact(@PathVariable("contactId") Long contactId,
                                                         @RequestParam("email") String newEmail,
                                                         Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            EmailAddress emailAddress = emailService.addNewEmailToContact(newEmail, contact);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    emailService.getEmailResponse(emailAddress)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @PutMapping("/update/email")
    public ResponseEntity<?> updateEmailAddress(
            @PathVariable("contactId") Long contactId,
            @RequestParam("id") Long emailId,
            @RequestParam("email") String newEmail,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            EmailAddress emailAddress = emailService.getEmailAddressById(emailId);
            EmailAddress svdEmail = emailService.updateEmailAddress(emailAddress, newEmail);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    emailService.getEmailResponse(svdEmail)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @DeleteMapping("/delete/email")
    public ResponseEntity<?> deleteEmailAddress(
            @PathVariable("contactId") Long contactId,
            @RequestParam("id") Long emailId,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            EmailAddress emailAddress = emailService.getEmailAddressById(emailId);
            emailService.deleteEmailAddress(emailAddress);
            return ResponseEntity.ok().body(new MessageResponse(true, HttpStatus.OK.value(),
                    "Email address successfully deleted."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @GetMapping("/emails")
    public ResponseEntity<?> getAllContactEmails(@PathVariable("contactId") Long contactId, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    emailService.getContactEmailAddresses(contact)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }
}
