package com.artsolo.phonecontacts.repositoryes;

import com.artsolo.phonecontacts.models.Contact;
import com.artsolo.phonecontacts.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsByNameAndUser(String name, User user);
    boolean existsByEmailAddressesEmailAndUser(String email, User user);
    boolean existsByPhoneNumbersPhoneNumberAndUser(String phoneNumber, User user);
    List<Contact> findByUser(User user);
}
