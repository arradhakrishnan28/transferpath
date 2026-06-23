package com.transferpath.transferpath;

import com.transferpath.transferpath.university.Program;
import com.transferpath.transferpath.university.ProgramRepository;
import com.transferpath.transferpath.university.University;
import com.transferpath.transferpath.university.UniversityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UniversityRepository universityRepository;
    private final ProgramRepository programRepository;

    public DataLoader(
            UniversityRepository universityRepository,
            ProgramRepository programRepository
    ) {
        this.universityRepository = universityRepository;
        this.programRepository = programRepository;
    }

    @Override
    public void run(String... args) {
        if (universityRepository.count() == 0) {
            seedUniversitiesAndPrograms();
        } else if (programRepository.count() == 0) {
            seedProgramsForExistingUniversities();
        }
    }

    private void seedUniversitiesAndPrograms() {
        University uiuc = universityRepository.save(
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

        University purdue = universityRepository.save(
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

        University wisconsin = universityRepository.save(
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

        University maryland = universityRepository.save(
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

        University arizonaState = universityRepository.save(
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

        seedPrograms(uiuc, purdue, wisconsin, maryland, arizonaState);
    }

    private void seedProgramsForExistingUniversities() {
        University uiuc = universityRepository.findByCountry("USA").stream()
                .filter(university -> university.getName().equals("University of Illinois Urbana-Champaign"))
                .findFirst()
                .orElseThrow();

        University purdue = universityRepository.findByCountry("USA").stream()
                .filter(university -> university.getName().equals("Purdue University"))
                .findFirst()
                .orElseThrow();

        University wisconsin = universityRepository.findByCountry("USA").stream()
                .filter(university -> university.getName().equals("University of Wisconsin-Madison"))
                .findFirst()
                .orElseThrow();

        University maryland = universityRepository.findByCountry("USA").stream()
                .filter(university -> university.getName().equals("University of Maryland"))
                .findFirst()
                .orElseThrow();

        University arizonaState = universityRepository.findByCountry("USA").stream()
                .filter(university -> university.getName().equals("Arizona State University"))
                .findFirst()
                .orElseThrow();

        seedPrograms(uiuc, purdue, wisconsin, maryland, arizonaState);
    }

    private void seedPrograms(
            University uiuc,
            University purdue,
            University wisconsin,
            University maryland,
            University arizonaState
    ) {
        programRepository.save(
                new Program(
                        "Computer Science",
                        "Bachelor's",
                        45000.0,
                        3.7,
                        "Highly Competitive",
                        "https://cs.illinois.edu",
                        uiuc
                )
        );

        programRepository.save(
                new Program(
                        "Computer Science",
                        "Bachelor's",
                        42000.0,
                        3.6,
                        "Highly Competitive",
                        "https://www.cs.purdue.edu",
                        purdue
                )
        );

        programRepository.save(
                new Program(
                        "Computer Science",
                        "Bachelor's",
                        43000.0,
                        3.6,
                        "Highly Competitive",
                        "https://www.cs.wisc.edu",
                        wisconsin
                )
        );

        programRepository.save(
                new Program(
                        "Computer Science",
                        "Bachelor's",
                        41000.0,
                        3.5,
                        "Highly Competitive",
                        "https://undergrad.cs.umd.edu",
                        maryland
                )
        );

        programRepository.save(
                new Program(
                        "Computer Science",
                        "Bachelor's",
                        33000.0,
                        3.2,
                        "Moderately Competitive",
                        "https://scai.engineering.asu.edu",
                        arizonaState
                )
        );

        programRepository.save(
                new Program(
                        "Data Science",
                        "Bachelor's",
                        44000.0,
                        3.6,
                        "Competitive",
                        "https://ischool.illinois.edu",
                        uiuc
                )
        );

        programRepository.save(
                new Program(
                        "Data Science",
                        "Bachelor's",
                        40000.0,
                        3.4,
                        "Competitive",
                        "https://www.purdue.edu",
                        purdue
                )
        );

        programRepository.save(
                new Program(
                        "Information Science",
                        "Bachelor's",
                        39000.0,
                        3.3,
                        "Competitive",
                        "https://ischool.umd.edu",
                        maryland
                )
        );

        programRepository.save(
                new Program(
                        "Software Engineering",
                        "Bachelor's",
                        32000.0,
                        3.1,
                        "Moderately Competitive",
                        "https://poly.engineering.asu.edu",
                        arizonaState
                )
        );
    }
}