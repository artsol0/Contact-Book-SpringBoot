package com.artsolo.phonecontacts.phone;

import com.artsolo.phonecontacts.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber,Long> {
    List<PhoneNumber> findAllByContact(Contact contact);
}
