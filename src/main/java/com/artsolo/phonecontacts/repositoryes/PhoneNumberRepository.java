package com.artsolo.phonecontacts.repositoryes;

import com.artsolo.phonecontacts.models.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber,Long> {

}
