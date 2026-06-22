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
                            "https://www.admissions.illinois.edu/apply/transfer",
                            true,
                            true,
                            false,
                            "University application",
                            "Strong GPA, major prerequisites, college coursework"
                    )
            );

            repository.save(
                    new University(
                            "Purdue University",
                            "USA",
                            3.4,
                            "Indiana",
                            "West Lafayette",
                            "June 1",
                            "https://www.admissions.purdue.edu",
                            true,
                            true,
                            false,
                            "University application",
                            "Calculus, programming courses preferred"
                    )
            );

            repository.save(
                    new University(
                            "University of Wisconsin-Madison",
                            "USA",
                            3.5,
                            "Wisconsin",
                            "Madison",
                            "February 1",
                            "https://admissions.wisc.edu",
                            true,
                            true,
                            false,
                            "University application",
                            "Strong academic record"
                    )
            );

            repository.save(
                    new University(
                            "University of Maryland",
                            "USA",
                            3.4,
                            "Maryland",
                            "College Park",
                            "March 1",
                            "https://admissions.umd.edu",
                            true,
                            true,
                            false,
                            "University application",
                            "Competitive transfer applicant"
                    )
            );

            repository.save(
                    new University(
                            "Arizona State University",
                            "USA",
                            3.0,
                            "Arizona",
                            "Tempe",
                            "Rolling",
                            "https://admission.asu.edu",
                            true,
                            true,
                            true,
                            "University application",
                            "College coursework required"
                    )
            );



        }
    }
}