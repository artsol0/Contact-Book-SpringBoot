package com.artsolo.phonecontacts.email;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactRepository;
import com.artsolo.phonecontacts.exceptions.NoDataFoundException;
import com.artsolo.phonecontacts.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmailAddressServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private EmailAddressRepository emailAddressRepository;

    @InjectMocks
    private EmailAddressService emailAddressService;

    @Test
    void getEmailAddressById() {
        Long emailId = 3L;
        EmailAddress emailAddress = EmailAddress.builder().id(emailId).email("email12@gmail.com").build();

        when(emailAddressRepository.findById(emailId)).thenReturn(Optional.of(emailAddress));
        EmailAddress result = emailAddressService.getEmailAddressById(emailId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(emailId);
        assertThat(result.getEmail()).isEqualTo("email12@gmail.com");
    }

    @Test
    void thrownExceptionWhileGettingEmailAddressById() {
        when(emailAddressRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> emailAddressService.getEmailAddressById(1L));
    }

    @Test
    void addNewEmailToContact() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        EmailAddress savedEmail = EmailAddress.builder().id(3L).contact(contact).email("email12@gmail.com").build();

        when(contactRepository.existsByEmailAddressesEmailAndUser("email12@gmail.com", user)).thenReturn(false);
        when(emailAddressRepository.save(any(EmailAddress.class))).thenReturn(savedEmail);

        EmailAddress result = emailAddressService.addNewEmailToContact("email12@gmail.com", contact);

        assertThat(result.getId()).isEqualTo(savedEmail.getId());
        assertThat(result.getEmail()).isEqualTo(savedEmail.getEmail());
        assertThat(result.getContact().getId()).isEqualTo(contact.getId());
    }

    @Test
    void thrownExceptionWhileAddingNewEmailToContact() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();

        when(contactRepository.existsByEmailAddressesEmailAndUser("email12@gmail.com", user)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                emailAddressService.addNewEmailToContact("email12@gmail.com", contact));
    }

    @Test
    void updateEmailAddress() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        EmailAddress emailAddress = EmailAddress.builder().id(3L).contact(contact).email("email12@gmail.com").build();
        EmailAddress savedEmail = EmailAddress.builder().id(3L).contact(contact).email("mail17@gmail.com").build();

        when(contactRepository.existsByEmailAddressesEmailAndUser("mail17@gmail.com", user)).thenReturn(false);
        when(emailAddressRepository.save(emailAddress)).thenReturn(savedEmail);

        EmailAddress result = emailAddressService.updateEmailAddress(emailAddress, "mail17@gmail.com");

        assertThat(result.getId()).isEqualTo(savedEmail.getId());
        assertThat(result.getEmail()).isEqualTo(savedEmail.getEmail());
        assertThat(result.getContact().getId()).isEqualTo(contact.getId());
    }

    @Test
    void thrownExceptionWhileUpdatingEmailAddress() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        EmailAddress emailAddress = EmailAddress.builder().id(3L).contact(contact).email("email12@gmail.com").build();

        when(contactRepository.existsByEmailAddressesEmailAndUser("mail17@gmail.com", user)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                emailAddressService.updateEmailAddress(emailAddress, "mail17@gmail.com"));
    }

    @Test
    void getContactEmailAddresses() {
        EmailAddress emailAddress1 = EmailAddress.builder().email("email12@gmail.com").build();
        EmailAddress emailAddress2 = EmailAddress.builder().email("email42@gmail.com").build();
        EmailAddress emailAddress3 = EmailAddress.builder().email("email51@gmail.com").build();

        Contact contact = Contact.builder().emailAddresses(Arrays.asList(emailAddress1, emailAddress2, emailAddress3)).build();

        when(emailAddressRepository.findAllByContact(contact)).thenReturn(Arrays.asList(emailAddress1, emailAddress2, emailAddress3));

        List<EmailAddressResponse> responses = emailAddressService.getContactEmailAddresses(contact);
        assertThat(responses.size()).isEqualTo(3);
        assertThat(responses).extracting(EmailAddressResponse::getEmail)
                .containsExactlyInAnyOrder("email12@gmail.com", "email42@gmail.com", "email51@gmail.com");
    }

    @Test
    void getEmailResponse() {
        EmailAddress emailAddress = EmailAddress.builder().id(3L).email("email12@gmail.com").build();
        EmailAddressResponse response = emailAddressService.getEmailResponse(emailAddress);
        assertThat(response.getId()).isEqualTo(emailAddress.getId());
        assertThat(response.getEmail()).isEqualTo(emailAddress.getEmail());
    }
}