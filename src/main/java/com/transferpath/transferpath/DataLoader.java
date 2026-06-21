package com.transferpath.transferpath;

import com.transferpath.transferpath.university.University;
import com.transferpath.transferpath.university.UniversityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UniversityRepository repository;

    public DataLoader(UniversityRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.save(
                new University(
                        "University of Illinois Urbana-Champaign",
                        "USA",
                        3.5
                )
        );
    }
}