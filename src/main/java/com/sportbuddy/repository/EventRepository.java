package com.sportbuddy.repository;

import com.sportbuddy.model.Event;
import com.sportbuddy.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findBySport(Sport sport);

    List<Event> findByCreatedById(Long userId);
}
