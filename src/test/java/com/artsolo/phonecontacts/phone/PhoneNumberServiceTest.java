package com.artsolo.phonecontacts.phone;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneNumberServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private PhoneNumberRepository phoneNumberRepository;

    @InjectMocks
    private PhoneNumberService phoneNumberService;

    @Test
    void getPhoneNumberById() {
        Long phoneId = 3L;
        PhoneNumber phoneNumber = PhoneNumber.builder().id(phoneId).phoneNumber("380425723").build();

        when(phoneNumberRepository.findById(phoneId)).thenReturn(Optional.of(phoneNumber));
        PhoneNumber result = phoneNumberService.getPhoneNumberById(phoneId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(phoneId);
        assertThat(result.getPhoneNumber()).isEqualTo("380425723");
    }

    @Test
    void thrownExceptionWhileGettingPhoneNumberById() {
        when(phoneNumberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> phoneNumberService.getPhoneNumberById(1L));
    }

    @Test
    void addNewPhoneToContact() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        PhoneNumber savedPhoneNumber = PhoneNumber.builder().id(3L).contact(contact).phoneNumber("380425723").build();

        when(contactRepository.existsByPhoneNumbersPhoneNumberAndUser("380425723", user)).thenReturn(false);
        when(phoneNumberRepository.save(any(PhoneNumber.class))).thenReturn(savedPhoneNumber);

        PhoneNumber result = phoneNumberService.addNewPhoneToContact("380425723", contact);

        assertThat(result.getId()).isEqualTo(savedPhoneNumber.getId());
        assertThat(result.getPhoneNumber()).isEqualTo(savedPhoneNumber.getPhoneNumber());
        assertThat(result.getContact().getId()).isEqualTo(contact.getId());
    }

    @Test
    void thrownExceptionWhileAddingNewPhoneToContact() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();

        when(contactRepository.existsByPhoneNumbersPhoneNumberAndUser("380425723", user)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                phoneNumberService.addNewPhoneToContact("380425723", contact));
    }

    @Test
    void updatePhoneNumber() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        PhoneNumber phoneNumber = PhoneNumber.builder().id(3L).contact(contact).phoneNumber("380425723").build();
        PhoneNumber savedPhoneNumber = PhoneNumber.builder().id(3L).contact(contact).phoneNumber("380758346").build();

        when(contactRepository.existsByPhoneNumbersPhoneNumberAndUser("380758346", user)).thenReturn(false);
        when(phoneNumberRepository.save(phoneNumber)).thenReturn(savedPhoneNumber);

        PhoneNumber result = phoneNumberService.updatePhoneNumber(phoneNumber, "380758346");

        assertThat(result.getId()).isEqualTo(savedPhoneNumber.getId());
        assertThat(result.getPhoneNumber()).isEqualTo(savedPhoneNumber.getPhoneNumber());
        assertThat(result.getContact().getId()).isEqualTo(contact.getId());
    }

    @Test
    void thrownExceptionWhileUpdatingPhoneNumber() {
        User user = User.builder().id(2L).username("user214").build();
        Contact contact = Contact.builder().id(4L).name("contact11").user(user).build();
        PhoneNumber phoneNumber = PhoneNumber.builder().id(3L).contact(contact).phoneNumber("380425723").build();

        when(contactRepository.existsByPhoneNumbersPhoneNumberAndUser("380758346", user)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                phoneNumberService.updatePhoneNumber(phoneNumber, "380758346"));
    }

    @Test
    void getContactPhoneNumbers() {
        PhoneNumber phoneNumber1 = PhoneNumber.builder().phoneNumber("380425723").build();
        PhoneNumber phoneNumber2 = PhoneNumber.builder().phoneNumber("380758346").build();
        PhoneNumber phoneNumber3 = PhoneNumber.builder().phoneNumber("+380-97-8432").build();

        Contact contact = Contact.builder().phoneNumbers(Arrays.asList(phoneNumber1, phoneNumber2, phoneNumber3)).build();

        when(phoneNumberRepository.findAllByContact(contact)).thenReturn(Arrays.asList(phoneNumber1, phoneNumber2, phoneNumber3));

        List<PhoneNumberResponse> responses = phoneNumberService.getContactPhoneNumbers(contact);
        assertThat(responses.size()).isEqualTo(3);
        assertThat(responses).extracting(PhoneNumberResponse::getPhone)
                .containsExactlyInAnyOrder("380425723", "380758346", "+380-97-8432");
    }

    @Test
    void getPhoneResponse() {
        PhoneNumber phoneNumber = PhoneNumber.builder().id(3L).phoneNumber("380425723").build();
        PhoneNumberResponse response = phoneNumberService.getPhoneResponse(phoneNumber);
        assertThat(response.getId()).isEqualTo(phoneNumber.getId());
        assertThat(response.getPhone()).isEqualTo(phoneNumber.getPhoneNumber());
    }
}