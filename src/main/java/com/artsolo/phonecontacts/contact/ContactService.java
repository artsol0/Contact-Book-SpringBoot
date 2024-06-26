package com.artsolo.phonecontacts.contact;

import com.artsolo.phonecontacts.contact.dto.AddContactRequest;
import com.artsolo.phonecontacts.contact.dto.ContactResponse;
import com.artsolo.phonecontacts.email.EmailAddress;
import com.artsolo.phonecontacts.email.EmailAddressResponse;
import com.artsolo.phonecontacts.exceptions.NoDataFoundException;
import com.artsolo.phonecontacts.phone.PhoneNumber;
import com.artsolo.phonecontacts.phone.PhoneNumberResponse;
import com.artsolo.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ImageService imageService;

    public Contact getContactById(Long id) {
        return contactRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Contact", id));
    }

    public Contact createNewContact(AddContactRequest request, User user) {
        if (contactRepository.existsByNameAndUser(request.getName(), user)) {
            throw new IllegalArgumentException("Name already taken by another contact.");
        }
        Contact contact = Contact.builder().name(request.getName()).user(user).build();

        List<EmailAddress> emailAddresses = request.getEmails().stream().map(email -> {
            if (contactRepository.existsByEmailAddressesEmailAndUser(email, user)) {
                throw new IllegalArgumentException("Email address already taken by another contact.");
            }
            return EmailAddress.builder().contact(contact).email(email).build();
        }).toList();

        List<PhoneNumber> phoneNumbers = request.getPhones().stream().map(phone -> {
            if (contactRepository.existsByPhoneNumbersPhoneNumberAndUser(phone, user)) {
                throw new IllegalArgumentException("Phone number already taken by another contact.");
            }
            return PhoneNumber.builder().contact(contact).phoneNumber(phone).build();
        }).toList();

        if (request.getImage() != null) {
            contact.setImagePath(imageService.saveImageToUserStorage(user.getUsername(), request.getImage()));
        } else {
            contact.setImagePath("src/main/resources/static/default-avatar-icon.jpg");
        }

        contact.setEmailAddresses(emailAddresses);
        contact.setPhoneNumbers(phoneNumbers);
        return contactRepository.save(contact);
    }

    public void deleteContact(Contact contact) {
        contactRepository.deleteById(contact.getId());
    }

    public boolean isUserContactOwner(Contact contact, User user) {
        return contact.getUser().getId().equals(user.getId());
    }

    public Contact renameContact(Contact contact, String newName) {
        contact.setName(newName);
        return contactRepository.save(contact);
    }

    public List<ContactResponse> getUserContacts(User user) {
        return contactRepository.findByUser(user).stream().map(this::getContactResponseFromContact).collect(Collectors.toList());
    }

    public List<ContactResponse> getUserContactsByName(String name, User user) {
        return contactRepository.findByNameContainingAndUser(name, user).stream().map(this::getContactResponseFromContact).collect(Collectors.toList());
    }

    public Contact changeContactImage(Contact contact, MultipartFile image) {
        Path imagePath = Path.of(contact.getImagePath());
        if (!imagePath.getFileName().toString().equals("default-avatar-icon.jpg")) {
            imageService.deleteImage(contact.getImagePath());
        }
        contact.setImagePath(imageService.saveImageToUserStorage(contact.getUser().getUsername(), image));
        return contactRepository.save(contact);
    }

    public ContactResponse getContactResponseFromContact(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .emails(contact.getEmailAddresses().stream().map(emailAddress ->
                        new EmailAddressResponse(emailAddress.getId(), emailAddress.getEmail())).toList())
                .phones(contact.getPhoneNumbers().stream().map(phoneNumber ->
                        new PhoneNumberResponse(phoneNumber.getId(), phoneNumber.getPhoneNumber())).toList())
                .imagePath(contact.getImagePath())
                .image(imageService.getImage(contact.getImagePath()))
                .build();
    }
}