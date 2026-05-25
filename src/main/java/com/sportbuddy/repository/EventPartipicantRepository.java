package com.sportbuddy.repository;

import com.sportbuddy.model.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventPartipicantRepository  extends JpaRepository<EventParticipant, Long> {

    List<EventParticipant> findByEventId(Long eventId);

    List<EventParticipant> findByUserId(Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}
