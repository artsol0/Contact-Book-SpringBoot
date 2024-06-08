package com.artsolo.phonecontacts.email;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactRepository;
import com.artsolo.phonecontacts.exceptions.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EmailAddressService {

    private final EmailAddressRepository emailRepository;
    private final ContactRepository contactRepository;

    public EmailAddress getEmailAddressById(Long id) {
        return emailRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Email address", id));
    }

    public EmailAddress addNewEmailToContact(String email, Contact contact) {
        if (!contactRepository.existsByEmailAddressesEmailAndUser(email, contact.getUser())) {
            EmailAddress emailAddress = EmailAddress.builder().contact(contact).email(email).build();
            return emailRepository.save(emailAddress);
        }
        throw new IllegalArgumentException("Email address is already taken by another contact.");
    }

    public EmailAddress updateEmailAddress(EmailAddress emailAddress, String newEmail) {
        if (!contactRepository.existsByEmailAddressesEmailAndUser(newEmail, emailAddress.getContact().getUser())) {
            emailAddress.setEmail(newEmail);
            return emailRepository.save(emailAddress);
        }
        throw new IllegalArgumentException("Email address is already taken by another contact.");
    }

    public void deleteEmailAddress(EmailAddress emailAddress) {
        emailRepository.deleteById(emailAddress.getId());
    }

    public List<EmailAddressResponse> getContactEmailAddresses(Contact contact) {
        return emailRepository.findAllByContact(contact).stream().map(this::getEmailResponse).collect(Collectors.toList());
    }

    public EmailAddressResponse getEmailResponse(EmailAddress emailAddress) {
        return EmailAddressResponse.builder()
                .id(emailAddress.getId())
                .email(emailAddress.getEmail())
                .build();
    }

}
