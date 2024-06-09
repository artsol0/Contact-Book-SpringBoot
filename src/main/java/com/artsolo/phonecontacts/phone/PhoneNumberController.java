package com.artsolo.phonecontacts.phone;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactService;
import com.artsolo.phonecontacts.responses.DataResponse;
import com.artsolo.phonecontacts.responses.MessageResponse;
import com.artsolo.phonecontacts.user.User;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact/{contactId}")
@Validated
public class PhoneNumberController {

    private final PhoneNumberService phoneService;
    private final ContactService contactService;

    @PostMapping("/add/phone")
    public ResponseEntity<?> addNewPhoneNumberToContact(
            @PathVariable("contactId") Long contactId,
            @RequestParam("phone") @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$") String newPhone,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            PhoneNumber phoneNumber = phoneService.addNewPhoneToContact(newPhone, contact);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    phoneService.getPhoneResponse(phoneNumber)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @PutMapping("/update/phone")
    public ResponseEntity<?> updatePhoneNumber(
            @PathVariable("contactId") Long contactId,
            @RequestParam("id") Long phoneId,
            @RequestParam("phone") @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$") String newPhone,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            PhoneNumber phoneNumber = phoneService.getPhoneNumberById(phoneId);
            PhoneNumber svdPhone = phoneService.updatePhoneNumber(phoneNumber, newPhone);
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    phoneService.getPhoneResponse(svdPhone)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @DeleteMapping("/delete/phone")
    public ResponseEntity<?> deletePhoneNumber(
            @PathVariable("contactId") Long contactId,
            @RequestParam("id") Long phoneId,
            Principal currentUser)
    {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            PhoneNumber phoneNumber = phoneService.getPhoneNumberById(phoneId);
            phoneService.deletePhoneNumber(phoneNumber);
            return ResponseEntity.ok().body(new MessageResponse(true, HttpStatus.OK.value(),
                    "Phone number successfully deleted."));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

    @GetMapping("/phones")
    public ResponseEntity<?> getAllContactPhones(@PathVariable("contactId") Long contactId, Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        Contact contact = contactService.getContactById(contactId);
        if (contactService.isUserContactOwner(contact, user)) {
            return ResponseEntity.ok().body(new DataResponse<>(true, HttpStatus.OK.value(),
                    phoneService.getContactPhoneNumbers(contact)));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(
                false, HttpStatus.FORBIDDEN.value(),
                "You are not the owner of the contact to perform this action."));
    }

}
