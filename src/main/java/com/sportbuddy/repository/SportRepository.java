package com.sportbuddy.repository;

import com.sportbuddy.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Long> {

    boolean existsByName(String name);

    Optional<Sport> findByName(String name);
}
