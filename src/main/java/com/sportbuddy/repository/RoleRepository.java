package com.sportbuddy.repository;

import com.sportbuddy.enums.RoleName;
import com.sportbuddy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

   Optional<Role> findByName(RoleName name);
}
