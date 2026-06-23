package com.transferpath.transferpath.university;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;
    private final ProgramRepository programRepository;

    public UniversityService(
            UniversityRepository universityRepository,
            ProgramRepository programRepository
    ) {
        this.universityRepository = universityRepository;
        this.programRepository = programRepository;
    }

    public List<UniversitySearchResult> searchUniversities(
            String country,
            Double gpa,
            Boolean international,
            Boolean fall,
            Boolean spring,
            String major
    ) {
        return universityRepository.filterUniversities(country, gpa, international, fall, spring)
                .stream()
                .map(university -> {
                    Optional<Program> matchedProgram = findBestProgramMatch(university, major);

                    Double fitScore = calculateFitScore(
                            university,
                            matchedProgram.orElse(null),
                            gpa,
                            international,
                            fall,
                            spring,
                            major
                    );

                    List<String> fitReasons = calculateFitReasons(
                            university,
                            matchedProgram.orElse(null),
                            gpa,
                            international,
                            fall,
                            spring,
                            major
                    );

                    return UniversitySearchResult.fromUniversity(
                            university,
                            fitScore,
                            fitReasons,
                            matchedProgram.orElse(null)
                    );
                })
                .sorted(Comparator.comparing(UniversitySearchResult::getFitScore).reversed())
                .toList();
    }

    private Optional<Program> findBestProgramMatch(University university, String major) {
        List<Program> programs = programRepository.findByUniversityId(university.getId());

        if (programs.isEmpty()) {
            return Optional.empty();
        }

        if (major == null || major.isBlank()) {
            return programs.stream()
                    .min(Comparator.comparing(Program::getMinimumRecommendedGpa));
        }

        String normalizedMajor = major.trim().toLowerCase();

        return programs.stream()
                .filter(program -> program.getMajorName() != null)
                .filter(program -> program.getMajorName().toLowerCase().contains(normalizedMajor))
                .findFirst()
                .or(() -> programs.stream()
                        .min(Comparator.comparing(Program::getMinimumRecommendedGpa)));
    }

    private Double calculateFitScore(
            University university,
            Program program,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring,
            String major
    ) {
        double score = 0.0;

        score += calculateUniversityGpaScore(university, studentGpa);
        score += calculateProgramGpaScore(program, studentGpa);
        score += calculateMajorScore(program, major);
        score += calculateInternationalScore(university, wantsInternational);
        score += calculateTermScore(university, wantsFall, wantsSpring);

        return Math.min(score, 100.0);
    }

    private List<String> calculateFitReasons(
            University university,
            Program program,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring,
            String major
    ) {
        List<String> reasons = new ArrayList<>();

        addUniversityGpaReason(reasons, university, studentGpa);
        addProgramGpaReason(reasons, program, studentGpa);
        addMajorReason(reasons, program, major);
        addInternationalReason(reasons, university, wantsInternational);
        addTermReasons(reasons, university, wantsFall, wantsSpring);
        addCostReason(reasons, program);
        addCompetitivenessReason(reasons, program);

        if (reasons.isEmpty()) {
            reasons.add("General match based on available university and program data");
        }

        return reasons;
    }

    private Double calculateUniversityGpaScore(University university, Double studentGpa) {
        if (studentGpa == null || university.getMinGpa() == null) {
            return 20.0;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < 0) {
            return 0.0;
        }

        if (difference >= 0.5) {
            return 25.0;
        }

        return 15.0 + (difference / 0.5) * 10.0;
    }

    private Double calculateProgramGpaScore(Program program, Double studentGpa) {
        if (program == null || studentGpa == null || program.getMinimumRecommendedGpa() == null) {
            return 10.0;
        }

        double difference = studentGpa - program.getMinimumRecommendedGpa();

        if (difference < 0) {
            return 0.0;
        }

        if (difference >= 0.5) {
            return 25.0;
        }

        return 15.0 + (difference / 0.5) * 10.0;
    }

    private Double calculateMajorScore(Program program, String major) {
        if (major == null || major.isBlank()) {
            return 10.0;
        }

        if (program == null || program.getMajorName() == null) {
            return 0.0;
        }

        String requestedMajor = major.trim().toLowerCase();
        String matchedMajor = program.getMajorName().toLowerCase();

        if (matchedMajor.equals(requestedMajor)) {
            return 20.0;
        }

        if (matchedMajor.contains(requestedMajor) || requestedMajor.contains(matchedMajor)) {
            return 15.0;
        }

        return 5.0;
    }

    private Double calculateInternationalScore(University university, Boolean wantsInternational) {
        if (wantsInternational == null) {
            return 10.0;
        }

        if (wantsInternational.equals(university.getAcceptsInternational())) {
            return 10.0;
        }

        return 0.0;
    }

    private Double calculateTermScore(University university, Boolean wantsFall, Boolean wantsSpring) {
        double score = 0.0;

        if (wantsFall == null && wantsSpring == null) {
            return 10.0;
        }

        if (Boolean.TRUE.equals(wantsFall) && Boolean.TRUE.equals(university.getAcceptsFall())) {
            score += 5.0;
        }

        if (Boolean.TRUE.equals(wantsSpring) && Boolean.TRUE.equals(university.getAcceptsSpring())) {
            score += 5.0;
        }

        return score;
    }

    private void addUniversityGpaReason(List<String> reasons, University university, Double studentGpa) {
        if (studentGpa == null || university.getMinGpa() == null) {
            reasons.add("University GPA was not used as a primary filter");
            return;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < 0) {
            reasons.add("Student GPA is below the university's listed minimum GPA");
        } else if (difference >= 0.5) {
            reasons.add("Student GPA is well above the university's listed minimum GPA");
        } else {
            reasons.add("Student GPA meets the university's listed minimum GPA");
        }
    }

    private void addProgramGpaReason(List<String> reasons, Program program, Double studentGpa) {
        if (program == null) {
            reasons.add("No specific program match was found for this university");
            return;
        }

        if (studentGpa == null || program.getMinimumRecommendedGpa() == null) {
            reasons.add("Program GPA recommendation was not available");
            return;
        }

        double difference = studentGpa - program.getMinimumRecommendedGpa();

        if (difference < 0) {
            reasons.add("Student GPA is below the recommended GPA for the matched program");
        } else if (difference >= 0.5) {
            reasons.add("Student GPA is well above the recommended GPA for the matched program");
        } else {
            reasons.add("Student GPA is close to the recommended GPA for the matched program");
        }
    }

    private void addMajorReason(List<String> reasons, Program program, String major) {
        if (major == null || major.isBlank()) {
            reasons.add("No major preference was entered, so the strongest available program was used");
            return;
        }

        if (program == null || program.getMajorName() == null) {
            reasons.add("No program data matched the requested major");
            return;
        }

        String requestedMajor = major.trim().toLowerCase();
        String matchedMajor = program.getMajorName().toLowerCase();

        if (matchedMajor.equals(requestedMajor)) {
            reasons.add("Exact major match found: " + program.getMajorName());
        } else if (matchedMajor.contains(requestedMajor) || requestedMajor.contains(matchedMajor)) {
            reasons.add("Related major match found: " + program.getMajorName());
        } else {
            reasons.add("Closest available program used: " + program.getMajorName());
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

    private void addCostReason(List<String> reasons, Program program) {
        if (program == null || program.getEstimatedAnnualCost() == null) {
            reasons.add("Program cost estimate is not available yet");
            return;
        }

        reasons.add("Estimated annual program cost: $" + Math.round(program.getEstimatedAnnualCost()));
    }

    private void addCompetitivenessReason(List<String> reasons, Program program) {
        if (program == null || program.getCompetitivenessLevel() == null) {
            reasons.add("Program competitiveness level is not available yet");
            return;
        }

        reasons.add("Program competitiveness: " + program.getCompetitivenessLevel());
    }
}