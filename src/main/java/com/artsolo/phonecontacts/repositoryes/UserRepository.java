package com.artsolo.phonecontacts.repositoryes;

import com.artsolo.phonecontacts.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
