package com.artsolo.phonecontacts.repositoryes;

import com.artsolo.phonecontacts.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByTitle(String title);
}
