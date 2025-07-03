package com.smartloansuite.user_service.repository;

import com.smartloansuite.user_service.entity.Role;
import com.smartloansuite.user_service.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
