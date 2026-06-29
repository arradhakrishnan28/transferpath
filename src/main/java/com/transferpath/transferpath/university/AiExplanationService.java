package com.transferpath.transferpath.university;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiExplanationService {

    public AiTransferExplanation explain(
            UniversitySearchResult result,
            String requestedMajor,
            Double studentGpa
    ) {
        List<String> strengths = buildStrengths(result, requestedMajor, studentGpa);
        List<String> risks = buildRisks(result, studentGpa);
        List<String> nextSteps = buildNextSteps(result, requestedMajor);

        String summary = buildSummary(result, strengths, risks);

        return new AiTransferExplanation(
                summary,
                strengths,
                risks,
                nextSteps
        );
    }

    private List<String> buildStrengths(
            UniversitySearchResult result,
            String requestedMajor,
            Double studentGpa
    ) {
        List<String> strengths = new ArrayList<>();

        if (result.getFitScore() != null && result.getFitScore() >= 85) {
            strengths.add("This school is currently a strong overall match based on the weighted fit score.");
        }

        if (studentGpa != null && result.getMinGpa() != null && studentGpa >= result.getMinGpa()) {
            strengths.add("Your GPA meets or exceeds the university's listed transfer GPA expectation.");
        }

        if (studentGpa != null
                && result.getMinimumRecommendedProgramGpa() != null
                && studentGpa >= result.getMinimumRecommendedProgramGpa()) {
            strengths.add("Your GPA also meets or exceeds the matched program's recommended GPA.");
        }

        if (requestedMajor != null
                && !requestedMajor.isBlank()
                && result.getMatchedMajor() != null
                && result.getMatchedMajor().equalsIgnoreCase(requestedMajor.trim())) {
            strengths.add("Your intended major has an exact program match at this university.");
        }

        if (Boolean.TRUE.equals(result.getAcceptsInternational())) {
            strengths.add("The university accepts international transfer students.");
        }

        if (Boolean.TRUE.equals(result.getAcceptsFall())) {
            strengths.add("The university supports fall transfer admission.");
        }

        if (strengths.isEmpty()) {
            strengths.add("This school has some baseline transfer compatibility, but the profile needs closer review.");
        }

        return strengths;
    }

    private List<String> buildRisks(UniversitySearchResult result, Double studentGpa) {
        List<String> risks = new ArrayList<>();

        if (studentGpa != null && result.getMinGpa() != null && studentGpa < result.getMinGpa()) {
            risks.add("Your GPA is below the university's listed transfer GPA expectation.");
        }

        if (studentGpa != null
                && result.getMinimumRecommendedProgramGpa() != null
                && studentGpa < result.getMinimumRecommendedProgramGpa()) {
            risks.add("Your GPA is below the matched program's recommended GPA.");
        }

        if (result.getProgramCompetitiveness() != null
                && result.getProgramCompetitiveness().toLowerCase().contains("extremely")) {
            risks.add("The matched program is marked as extremely competitive.");
        }

        if (result.getEstimatedAnnualCost() != null && result.getEstimatedAnnualCost() >= 60000) {
            risks.add("The estimated annual cost is high compared with other options in the dataset.");
        }

        if (Boolean.FALSE.equals(result.getAcceptsSpring())) {
            risks.add("Spring transfer admission is not available for this university.");
        }

        if (risks.isEmpty()) {
            risks.add("No major risk flags were detected from the current dataset.");
        }

        return risks;
    }

    private List<String> buildNextSteps(UniversitySearchResult result, String requestedMajor) {
        List<String> nextSteps = new ArrayList<>();

        nextSteps.add("Review the official transfer admissions page before applying.");

        if (requestedMajor != null && !requestedMajor.isBlank()) {
            nextSteps.add("Compare your completed coursework against the requirements for " + requestedMajor.trim() + ".");
        }

        if (result.getTransferRequirementAnalysis() != null) {
            nextSteps.add("Use the parsed requirement summary to identify missing prerequisites or recommended courses.");
        }

        if (result.getApplicationDeadline() != null && !result.getApplicationDeadline().isBlank()) {
            nextSteps.add("Plan backward from the listed deadline: " + result.getApplicationDeadline() + ".");
        }

        if (result.getProgramUrl() != null && !result.getProgramUrl().isBlank()) {
            nextSteps.add("Check the matched program page for major-specific transfer expectations.");
        }

        return nextSteps;
    }

    private String buildSummary(
            UniversitySearchResult result,
            List<String> strengths,
            List<String> risks
    ) {
        String schoolName = result.getName() == null ? "This university" : result.getName();
        String fitScore = result.getFitScore() == null ? "an unavailable" : result.getFitScore().toString();

        return schoolName
                + " has a fit score of "
                + fitScore
                + ". The strongest signal is: "
                + strengths.get(0)
                + " Main risk to review: "
                + risks.get(0);
    }
}