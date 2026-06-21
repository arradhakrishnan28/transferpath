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
        if (repository.count() == 0) {
            repository.save(
                    new University(
                            "University of Illinois Urbana-Champaign",
                            "USA",
                            3.5,
                            "Illinois",
                            "Urbana-Champaign",
                            "March 1",
                            "https://www.admissions.illinois.edu/apply/transfer"
                    )
            );
        }
    }
}