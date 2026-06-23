package com.transferpath.transferpath.university;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                .map(university -> {
                    Double fitScore = calculateFitScore(university, gpa, international, fall, spring);
                    List<String> fitReasons = calculateFitReasons(university, gpa, international, fall, spring);

                    return UniversitySearchResult.fromUniversity(university, fitScore, fitReasons);
                })
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

    private List<String> calculateFitReasons(
            University university,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring
    ) {
        List<String> reasons = new ArrayList<>();

        addGpaReason(reasons, university, studentGpa);
        addInternationalReason(reasons, university, wantsInternational);
        addTermReasons(reasons, university, wantsFall, wantsSpring);

        if (reasons.isEmpty()) {
            reasons.add("General match based on available university data");
        }

        return reasons;
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

    private void addGpaReason(List<String> reasons, University university, Double studentGpa) {
        if (studentGpa == null || university.getMinGpa() == null) {
            reasons.add("GPA was not used as a primary filter");
            return;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < 0) {
            reasons.add("Student GPA is below the listed minimum GPA");
        } else if (difference >= 0.5) {
            reasons.add("Student GPA is well above the listed minimum GPA");
        } else {
            reasons.add("Student GPA meets the listed minimum GPA");
        }
    }

    private void addInternationalReason(
            List<String> reasons,
            University university,
            Boolean wantsInternational
    ) {
        if (wantsInternational == null) {
            reasons.add("International student preference was not specified");
            return;
        }

        if (Boolean.TRUE.equals(wantsInternational) && Boolean.TRUE.equals(university.getAcceptsInternational())) {
            reasons.add("Accepts international transfer students");
        } else if (Boolean.FALSE.equals(wantsInternational)) {
            reasons.add("International student acceptance was not required");
        } else {
            reasons.add("Does not match the international student preference");
        }
    }

    private void addTermReasons(
            List<String> reasons,
            University university,
            Boolean wantsFall,
            Boolean wantsSpring
    ) {
        if (wantsFall == null && wantsSpring == null) {
            reasons.add("Admission term preference was not specified");
            return;
        }

        if (Boolean.TRUE.equals(wantsFall)) {
            if (Boolean.TRUE.equals(university.getAcceptsFall())) {
                reasons.add("Supports fall transfer admission");
            } else {
                reasons.add("Does not list fall transfer admission");
            }
        }

        if (Boolean.TRUE.equals(wantsSpring)) {
            if (Boolean.TRUE.equals(university.getAcceptsSpring())) {
                reasons.add("Supports spring transfer admission");
            } else {
                reasons.add("Does not list spring transfer admission");
            }
        }
    }
}