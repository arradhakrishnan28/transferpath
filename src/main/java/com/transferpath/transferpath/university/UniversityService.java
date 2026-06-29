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
    private final TransferRequirementParser transferRequirementParser;
    private final AiExplanationService aiExplanationService;

    public UniversityService(
            UniversityRepository universityRepository,
            ProgramRepository programRepository,
            TransferRequirementParser transferRequirementParser,
            AiExplanationService aiExplanationService
    ) {
        this.universityRepository = universityRepository;
        this.programRepository = programRepository;
        this.transferRequirementParser = transferRequirementParser;
        this.aiExplanationService = aiExplanationService;
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

                    ScoreBreakdown scoreBreakdown = calculateScoreBreakdown(
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
                            scoreBreakdown,
                            gpa,
                            international,
                            fall,
                            spring,
                            major
                    );

                    TransferRequirementAnalysis requirementAnalysis =
                            transferRequirementParser.analyze(university.getTransferRequirements());

                    UniversitySearchResult resultWithoutAi = UniversitySearchResult.fromUniversity(
                            university,
                            scoreBreakdown,
                            fitReasons,
                            matchedProgram.orElse(null),
                            requirementAnalysis
                    );

                    AiTransferExplanation aiExplanation = aiExplanationService.explain(
                            resultWithoutAi,
                            major,
                            gpa
                    );

                    return UniversitySearchResult.fromUniversity(
                            university,
                            scoreBreakdown,
                            fitReasons,
                            matchedProgram.orElse(null),
                            requirementAnalysis,
                            aiExplanation
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

    private ScoreBreakdown calculateScoreBreakdown(
            University university,
            Program program,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring,
            String major
    ) {
        return new ScoreBreakdown(
                calculateUniversityGpaScore(university, studentGpa),
                calculateProgramGpaScore(program, studentGpa),
                calculateMajorScore(program, major),
                calculateInternationalScore(university, wantsInternational),
                calculateTermScore(university, wantsFall, wantsSpring),
                calculateCostScore(program),
                calculateCompetitivenessScore(program)
        );
    }

    private List<String> calculateFitReasons(
            University university,
            Program program,
            ScoreBreakdown scoreBreakdown,
            Double studentGpa,
            Boolean wantsInternational,
            Boolean wantsFall,
            Boolean wantsSpring,
            String major
    ) {
        List<String> reasons = new ArrayList<>();

        addUniversityGpaReason(reasons, university, studentGpa, scoreBreakdown.getUniversityGpa());
        addProgramGpaReason(reasons, program, studentGpa, scoreBreakdown.getProgramGpa());
        addMajorReason(reasons, program, major, scoreBreakdown.getMajorMatch());
        addInternationalReason(reasons, university, wantsInternational, scoreBreakdown.getInternational());
        addTermReasons(reasons, university, wantsFall, wantsSpring, scoreBreakdown.getTermMatch());
        addCostReason(reasons, program, scoreBreakdown.getCost());
        addCompetitivenessReason(reasons, program, scoreBreakdown.getCompetitiveness());

        return reasons;
    }

    private Double calculateUniversityGpaScore(University university, Double studentGpa) {
        if (studentGpa == null || university.getMinGpa() == null) {
            return 12.0;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < -0.3) {
            return 0.0;
        }

        if (difference < 0) {
            return 6.0;
        }

        if (difference >= 0.5) {
            return 18.0;
        }

        return 12.0 + (difference / 0.5) * 6.0;
    }

    private Double calculateProgramGpaScore(Program program, Double studentGpa) {
        if (program == null || studentGpa == null || program.getMinimumRecommendedGpa() == null) {
            return 10.0;
        }

        double difference = studentGpa - program.getMinimumRecommendedGpa();

        if (difference < -0.3) {
            return 0.0;
        }

        if (difference < 0) {
            return 8.0;
        }

        if (difference >= 0.5) {
            return 22.0;
        }

        return 14.0 + (difference / 0.5) * 8.0;
    }

    private Double calculateMajorScore(Program program, String major) {
        if (major == null || major.isBlank()) {
            return 8.0;
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
            return 8.0;
        }

        if (wantsInternational.equals(university.getAcceptsInternational())) {
            return 10.0;
        }

        return 0.0;
    }

    private Double calculateTermScore(University university, Boolean wantsFall, Boolean wantsSpring) {
        if (wantsFall == null && wantsSpring == null) {
            return 8.0;
        }

        double score = 0.0;

        if (Boolean.TRUE.equals(wantsFall) && Boolean.TRUE.equals(university.getAcceptsFall())) {
            score += 5.0;
        }

        if (Boolean.TRUE.equals(wantsSpring) && Boolean.TRUE.equals(university.getAcceptsSpring())) {
            score += 5.0;
        }

        return score;
    }

    private Double calculateCostScore(Program program) {
        if (program == null || program.getEstimatedAnnualCost() == null) {
            return 5.0;
        }

        double cost = program.getEstimatedAnnualCost();

        if (cost <= 35000) {
            return 10.0;
        }

        if (cost <= 45000) {
            return 8.0;
        }

        if (cost <= 55000) {
            return 6.0;
        }

        if (cost <= 65000) {
            return 4.0;
        }

        return 2.0;
    }

    private Double calculateCompetitivenessScore(Program program) {
        if (program == null || program.getCompetitivenessLevel() == null) {
            return 4.0;
        }

        String competitiveness = program.getCompetitivenessLevel().toLowerCase();

        if (competitiveness.contains("moderately")) {
            return 10.0;
        }

        if (competitiveness.contains("competitive")
                && !competitiveness.contains("highly")
                && !competitiveness.contains("extremely")) {
            return 8.0;
        }

        if (competitiveness.contains("highly")) {
            return 6.0;
        }

        if (competitiveness.contains("extremely")) {
            return 4.0;
        }

        return 5.0;
    }

    private void addUniversityGpaReason(
            List<String> reasons,
            University university,
            Double studentGpa,
            Double score
    ) {
        if (studentGpa == null || university.getMinGpa() == null) {
            reasons.add("University GPA contribution: " + score + " points because GPA was not provided or the school has no listed minimum.");
            return;
        }

        double difference = studentGpa - university.getMinGpa();

        if (difference < 0) {
            reasons.add("University GPA contribution: " + score + " points because the student GPA is below the university minimum.");
        } else if (difference >= 0.5) {
            reasons.add("University GPA contribution: " + score + " points because the student GPA is well above the university minimum.");
        } else {
            reasons.add("University GPA contribution: " + score + " points because the student GPA meets the university minimum.");
        }
    }

    private void addProgramGpaReason(
            List<String> reasons,
            Program program,
            Double studentGpa,
            Double score
    ) {
        if (program == null) {
            reasons.add("Program GPA contribution: " + score + " points because no specific program match was found.");
            return;
        }

        if (studentGpa == null || program.getMinimumRecommendedGpa() == null) {
            reasons.add("Program GPA contribution: " + score + " points because program GPA data is incomplete.");
            return;
        }

        double difference = studentGpa - program.getMinimumRecommendedGpa();

        if (difference < 0) {
            reasons.add("Program GPA contribution: " + score + " points because the student GPA is below the program recommendation.");
        } else if (difference >= 0.5) {
            reasons.add("Program GPA contribution: " + score + " points because the student GPA is well above the program recommendation.");
        } else {
            reasons.add("Program GPA contribution: " + score + " points because the student GPA is close to the program recommendation.");
        }
    }

    private void addMajorReason(
            List<String> reasons,
            Program program,
            String major,
            Double score
    ) {
        if (major == null || major.isBlank()) {
            reasons.add("Major match contribution: " + score + " points because no major preference was entered.");
            return;
        }

        if (program == null || program.getMajorName() == null) {
            reasons.add("Major match contribution: " + score + " points because no matching program data was available.");
            return;
        }

        reasons.add("Major match contribution: " + score + " points for matched program: " + program.getMajorName() + ".");
    }

    private void addInternationalReason(
            List<String> reasons,
            University university,
            Boolean wantsInternational,
            Double score
    ) {
        if (wantsInternational == null) {
            reasons.add("International contribution: " + score + " points because international preference was not specified.");
            return;
        }

        if (Boolean.TRUE.equals(wantsInternational) && Boolean.TRUE.equals(university.getAcceptsInternational())) {
            reasons.add("International contribution: " + score + " points because the university accepts international transfers.");
        } else if (Boolean.FALSE.equals(wantsInternational)) {
            reasons.add("International contribution: " + score + " points because international acceptance was not required.");
        } else {
            reasons.add("International contribution: " + score + " points because the university does not match the international preference.");
        }
    }

    private void addTermReasons(
            List<String> reasons,
            University university,
            Boolean wantsFall,
            Boolean wantsSpring,
            Double score
    ) {
        if (wantsFall == null && wantsSpring == null) {
            reasons.add("Admission term contribution: " + score + " points because no term preference was specified.");
            return;
        }

        reasons.add("Admission term contribution: " + score + " points based on fall/spring transfer availability.");
    }

    private void addCostReason(List<String> reasons, Program program, Double score) {
        if (program == null || program.getEstimatedAnnualCost() == null) {
            reasons.add("Cost contribution: " + score + " points because cost data is unavailable.");
            return;
        }

        reasons.add("Cost contribution: " + score + " points for estimated annual cost of $" + Math.round(program.getEstimatedAnnualCost()) + ".");
    }

    private void addCompetitivenessReason(List<String> reasons, Program program, Double score) {
        if (program == null || program.getCompetitivenessLevel() == null) {
            reasons.add("Competitiveness contribution: " + score + " points because competitiveness data is unavailable.");
            return;
        }

        reasons.add("Competitiveness contribution: " + score + " points for a " + program.getCompetitivenessLevel() + " program.");
    }
}