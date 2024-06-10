package com.artsolo.phonecontacts.contact;

import com.artsolo.phonecontacts.email.EmailAddress;
import com.artsolo.phonecontacts.phone.PhoneNumber;
import com.artsolo.phonecontacts.user.User;
import com.artsolo.phonecontacts.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = User.builder().username("user214").build();
        User user2 = User.builder().username("user453").build();

        EmailAddress emailAddress1 = EmailAddress.builder().email("email12@gmail.com").build();
        EmailAddress emailAddress2 = EmailAddress.builder().email("email42@gmail.com").build();
        EmailAddress emailAddress3 = EmailAddress.builder().email("email51@gmail.com").build();

        PhoneNumber phoneNumber1 = PhoneNumber.builder().phoneNumber("380425723").build();
        PhoneNumber phoneNumber2 = PhoneNumber.builder().phoneNumber("380758346").build();
        PhoneNumber phoneNumber3 = PhoneNumber.builder().phoneNumber("+380-97-8432").build();

        Contact contact1 = Contact.builder()
                .name("contact11")
                .user(user1)
                .emailAddresses(Arrays.asList(emailAddress1, emailAddress2))
                .build();

        emailAddress1.setContact(contact1);
        emailAddress2.setContact(contact1);

        Contact contact2 = Contact.builder()
                .name("contact12")
                .user(user1)
                .phoneNumbers(Arrays.asList(phoneNumber1, phoneNumber2))
                .build();

        phoneNumber1.setContact(contact2);
        phoneNumber2.setContact(contact2);

        Contact contact3 = Contact.builder()
                .name("contact3")
                .user(user2)
                .emailAddresses(Arrays.asList(emailAddress3))
                .phoneNumbers(Arrays.asList(phoneNumber3))
                .build();

        emailAddress3.setContact(contact3);
        phoneNumber3.setContact(contact3);

        Contact contact4 = Contact.builder().name("contact22").user(user1).build();

        userRepository.saveAll(Arrays.asList(user1, user2));
        contactRepository.saveAll(Arrays.asList(contact1, contact2, contact3, contact4));
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void existsByNameAndUser() {
        User user1 = userRepository.findByUsername("user214").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByNameAndUser("contact11", user1);
        assertThat(isContactExist).isTrue();
    }

    @Test
    void notExistByNameAndUser() {
        User user2 = userRepository.findByUsername("user453").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByNameAndUser("contact11", user2);
        assertThat(isContactExist).isFalse();
    }

    @Test
    void existsByEmailAddressesEmailAndUser() {
        User user1 = userRepository.findByUsername("user214").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByEmailAddressesEmailAndUser("email42@gmail.com", user1);
        assertThat(isContactExist).isTrue();
    }

    @Test
    void notExistsByEmailAddressesEmailAndUser() {
        User user1 = userRepository.findByUsername("user214").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByEmailAddressesEmailAndUser("email51@gmail.com", user1);
        assertThat(isContactExist).isFalse();
    }

    @Test
    void existsByPhoneNumbersPhoneNumberAndUser() {
        User user2 = userRepository.findByUsername("user453").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByPhoneNumbersPhoneNumberAndUser("+380-97-8432", user2);
        assertThat(isContactExist).isTrue();
    }

    @Test
    void notExistsByPhoneNumbersPhoneNumberAndUser() {
        User user2 = userRepository.findByUsername("user453").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isContactExist = contactRepository.existsByPhoneNumbersPhoneNumberAndUser("380425723", user2);
        assertThat(isContactExist).isFalse();
    }

    @Test
    void findByUser() {
        User user1 = userRepository.findByUsername("user214").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Contact> userContacts = contactRepository.findByUser(user1);
        userContacts.forEach(contact -> assertThat(contact.getUser().getId()).isEqualTo(user1.getId()));
    }

    @Test
    void findByNameContainingAndUser() {
        User user1 = userRepository.findByUsername("user214").orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Contact> userContacts = contactRepository.findByNameContainingAndUser("contact1", user1);
        assertThat(userContacts.size()).isEqualTo(2);
        userContacts.forEach(contact -> assertThat(contact.getName()).containsIgnoringCase("contact1"));
        userContacts.forEach(contact -> assertThat(contact.getUser().getId()).isEqualTo(user1.getId()));
    }
}