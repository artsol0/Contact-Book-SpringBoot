package com.artsolo.phonecontacts.phone;

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
class PhoneNumberRepositoryTest {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private ContactRepository contactRepository;

    private Contact savedContact1;
    private Contact savedContact2;

    @BeforeEach
    void setUp() {

        PhoneNumber phoneNumber1 = PhoneNumber.builder().phoneNumber("380425723").build();
        PhoneNumber phoneNumber2 = PhoneNumber.builder().phoneNumber("380758346").build();
        PhoneNumber phoneNumber3 = PhoneNumber.builder().phoneNumber("+380-97-8432").build();

        Contact contact1 = Contact.builder().phoneNumbers(Arrays.asList(phoneNumber1, phoneNumber2)).build();
        phoneNumber1.setContact(contact1);
        phoneNumber2.setContact(contact1);

        Contact contact2 = Contact.builder().phoneNumbers(Arrays.asList(phoneNumber3)).build();
        phoneNumber3.setContact(contact2);

        savedContact1 = contactRepository.save(contact1);
        savedContact2 = contactRepository.save(contact2);
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll();
    }

    @Test
    void findAllByContact1() {
        List<PhoneNumber> phoneNumbers = phoneNumberRepository.findAllByContact(savedContact1);
        assertThat(phoneNumbers.size()).isEqualTo(2);
        assertThat(phoneNumbers).extracting(PhoneNumber::getPhoneNumber)
                .containsExactlyInAnyOrder("380425723", "380758346");
    }

    @Test
    void findAllByContact2() {
        List<PhoneNumber> phoneNumbers = phoneNumberRepository.findAllByContact(savedContact2);
        assertThat(phoneNumbers.size()).isEqualTo(1);
        assertThat(phoneNumbers).extracting(PhoneNumber::getPhoneNumber)
                .containsExactlyInAnyOrder("+380-97-8432");
    }
}