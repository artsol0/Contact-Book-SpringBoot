package com.artsolo.phonecontacts.repositoryes;

import com.artsolo.phonecontacts.models.EmailAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAddressRepository extends JpaRepository<EmailAddress,Long> {

}
