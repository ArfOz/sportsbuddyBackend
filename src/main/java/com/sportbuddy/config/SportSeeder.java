package com.sportbuddy.config;

import com.sportbuddy.model.Sport;
import com.sportbuddy.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SportSeeder implements CommandLineRunner {

    private final SportRepository sportRepository;

    @Override
    public void run(String... args) {

        List<String> defaultSports = List.of(
                "FOOTBALL",
                "TENNIS",
                "SWIMMING",
                "FITNESS",
                "RUNNING",
                "CYCLING",
                "HIKING",
                "BASKETBALL",
                "VOLLEYBALL"
        );

        for (String name : defaultSports) {
            if (!sportRepository.existsByName(name)) {
                Sport sport = new Sport();
                sport.setName(name);
                sportRepository.save(sport);
            }
        }
    }
}
