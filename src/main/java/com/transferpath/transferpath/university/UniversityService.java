package com.transferpath.transferpath.university;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class UniversityService {

    private final UniversityRepository repository;

    public UniversityService(UniversityRepository repository) {
        this.repository = repository;
    }

    public List<UniversitySearchResult> searchUniversities(
            String country,
            Double gpa,
            Boolean international,
            Boolean fall,
            Boolean spring
    ) {
        return repository.filterUniversities(country, gpa, international, fall, spring)
                .stream()
                .map(university -> UniversitySearchResult.fromUniversity(
                        university,
                        calculateFitScore(university, gpa, international, fall, spring)
                ))
                .sorted(Comparator.comparing(UniversitySearchResult::getFitScore).reversed())
                .toList();
    }

    private Double calculateFitScore(
            University university,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring
    ) {
        double score = 0.0;

        score += calculateGpaScore(university, studentGpa);
        score += calculateInternationalScore(university, wantsInternational);
        score += calculateTermScore(university, wantsFall, wantsSpring);

        return Math.min(score, 100.0);
    }

    private Double calculateGpaScore(University university, Double studentGpa) {
        if (studentGpa == null || university.getMinGpa() == null) {
            return 40.0;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < 0) {
            return 0.0;
        }

        if (difference >= 0.5) {
            return 60.0;
        }

        return 40.0 + (difference / 0.5) * 20.0;
    }

    private Double calculateInternationalScore(University university, Boolean wantsInternational) {
        if (wantsInternational == null) {
            return 20.0;
        }

        if (wantsInternational.equals(university.getAcceptsInternational())) {
            return 20.0;
        }

        return 0.0;
    }

    private Double calculateTermScore(University university, Boolean wantsFall, Boolean wantsSpring) {
        double score = 0.0;

        if (wantsFall == null && wantsSpring == null) {
            return 20.0;
        }

        if (Boolean.TRUE.equals(wantsFall) && Boolean.TRUE.equals(university.getAcceptsFall())) {
            score += 10.0;
        }

        if (Boolean.TRUE.equals(wantsSpring) && Boolean.TRUE.equals(university.getAcceptsSpring())) {
            score += 10.0;
        }

        return score;
    }
}