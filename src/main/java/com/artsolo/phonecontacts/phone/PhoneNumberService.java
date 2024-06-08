package com.artsolo.phonecontacts.phone;

import com.artsolo.phonecontacts.contact.Contact;
import com.artsolo.phonecontacts.contact.ContactRepository;
import com.artsolo.phonecontacts.exceptions.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhoneNumberService {

    private final PhoneNumberRepository phoneRepository;
    private final ContactRepository contactRepository;

    public PhoneNumber getPhoneNumberById(Long id) {
        return phoneRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Phone number", id));
    }

    public PhoneNumber addNewPhoneToContact(String phone, Contact contact) {
        if (!contactRepository.existsByPhoneNumbersPhoneNumberAndUser(phone, contact.getUser())) {
            PhoneNumber phoneNumber = PhoneNumber.builder().contact(contact).phoneNumber(phone).build();
            return phoneRepository.save(phoneNumber);
        }
        throw new IllegalArgumentException("Phone number is already taken by another contact.");
    }

    public PhoneNumber updatePhoneNumber(PhoneNumber phoneNumber, String newPhone) {
        if (!contactRepository.existsByPhoneNumbersPhoneNumberAndUser(newPhone, phoneNumber.getContact().getUser())) {
            phoneNumber.setPhoneNumber(newPhone);
            return phoneRepository.save(phoneNumber);
        }
        throw new IllegalArgumentException("Phone number is already taken by another contact.");
    }

    public void deletePhoneNumber(PhoneNumber phoneNumber) {
        phoneRepository.deleteById(phoneNumber.getId());
    }

    public List<PhoneNumberResponse> getContactPhoneNumbers(Contact contact) {
        return phoneRepository.findAllByContact(contact).stream().map(this::getPhoneResponse).collect(Collectors.toList());
    }

    public PhoneNumberResponse getPhoneResponse(PhoneNumber phoneNumber) {
        return PhoneNumberResponse.builder()
                .id(phoneNumber.getId())
                .phone(phoneNumber.getPhoneNumber())
                .build();
    }

}
