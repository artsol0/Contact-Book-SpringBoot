package com.artsolo.phonecontacts.email;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmailAddressRepositoryTest {

    @Autowired
    private EmailAddressRepository emailAddressRepository;

    @Autowired
    private ContactRepository contactRepository;

    private Contact savedContact1;
    private Contact savedContact2;

    @BeforeEach
    void setUp() {
        EmailAddress emailAddress1 = EmailAddress.builder().email("email12@gmail.com").build();
        EmailAddress emailAddress2 = EmailAddress.builder().email("email42@gmail.com").build();
        EmailAddress emailAddress3 = EmailAddress.builder().email("email51@gmail.com").build();

        Contact contact1 = Contact.builder().emailAddresses(Arrays.asList(emailAddress1, emailAddress2)).build();
        emailAddress1.setContact(contact1);
        emailAddress2.setContact(contact1);

        Contact contact2 = Contact.builder().emailAddresses(Arrays.asList(emailAddress3)).build();
        emailAddress3.setContact(contact2);

        savedContact1 = contactRepository.save(contact1);
        savedContact2 = contactRepository.save(contact2);
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll();
    }

    @Test
    void findAllByContact1() {
        List<EmailAddress> emailAddresses = emailAddressRepository.findAllByContact(savedContact1);
        assertThat(emailAddresses.size()).isEqualTo(2);
        assertThat(emailAddresses).extracting(EmailAddress::getEmail)
                .containsExactlyInAnyOrder("email12@gmail.com", "email42@gmail.com");
    }

    @Test
    void findAllByContact2() {
        List<EmailAddress> emailAddresses = emailAddressRepository.findAllByContact(savedContact2);
        assertThat(emailAddresses.size()).isEqualTo(1);
        assertThat(emailAddresses).extracting(EmailAddress::getEmail)
                .containsExactlyInAnyOrder("email51@gmail.com");
    }
}