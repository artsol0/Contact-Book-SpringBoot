package com.artsolo.phonecontacts.email;

import com.artsolo.phonecontacts.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailAddressRepository extends JpaRepository<EmailAddress,Long> {
    List<EmailAddress> findAllByContact(Contact contact);
}
