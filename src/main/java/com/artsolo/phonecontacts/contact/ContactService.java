package com.artsolo.phonecontacts.contact;

import com.artsolo.phonecontacts.contact.dto.AddContactRequest;
import com.artsolo.phonecontacts.contact.dto.ContactResponse;
import com.artsolo.phonecontacts.email.EmailAddress;
import com.artsolo.phonecontacts.exceptions.NoDataFoundException;
import com.artsolo.phonecontacts.phone.PhoneNumber;
import com.artsolo.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

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

        contact.setEmailAddresses(emailAddresses);
        contact.setPhoneNumbers(phoneNumbers);
        return contactRepository.save(contact);
    }

    public ContactResponse getContactResponseFromContact(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .name(contact.getName())
                .emails(contact.getEmailAddresses().stream().map(EmailAddress::getEmail).toList())
                .phones(contact.getPhoneNumbers().stream().map(PhoneNumber::getPhoneNumber).toList())
                .build();
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+38[0-9-]+$";
        return phoneNumber.matches(phoneRegex);
    }
}