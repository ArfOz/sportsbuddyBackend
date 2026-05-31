package com.sportbuddy.config;

import com.sportbuddy.enums.RoleName;
import com.sportbuddy.model.Role;
import com.sportbuddy.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (RoleName rn : RoleName.values()) {
            roleRepository.findByName(rn)
                    .orElseGet(() -> roleRepository.save(new Role(rn)));
        }
    }
}
