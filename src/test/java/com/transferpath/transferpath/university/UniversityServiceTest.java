package com.transferpath.transferpath.university;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniversityServiceTest {

    @Test
    void searchUniversitiesReturnsResultsSortedByFitScoreDescending() {
        UniversityRepository universityRepository = mock(UniversityRepository.class);
        ProgramRepository programRepository = mock(ProgramRepository.class);
        TransferRequirementParser transferRequirementParser = new TransferRequirementParser();
        AiExplanationService aiExplanationService = new AiExplanationService();

        UniversityService service = new UniversityService(
                universityRepository,
                programRepository,
                transferRequirementParser,
                aiExplanationService
        );

        University strongMatch = new University(
                "Strong Match University",
                "USA",
                3.0,
                "California",
                "Stanford",
                "March 1",
                "https://example.com/strong",
                true,
                true,
                true,
                "University application",
                "Strong GPA, major prerequisites, and rigorous college coursework"
        );

        University weakerMatch = new University(
                "Weaker Match University",
                "USA",
                3.5,
                "New York",
                "New York",
                "April 1",
                "https://example.com/weaker",
                true,
                true,
                false,
                "University application",
                "Competitive transfer applicant with strong academic record"
        );

        Program strongProgram = new Program(
                "Computer Science",
                "Bachelor's",
                35000.0,
                3.2,
                "Competitive",
                "https://example.com/strong-cs",
                strongMatch
        );

        Program weakerProgram = new Program(
                "Computer Science",
                "Bachelor's",
                65000.0,
                3.7,
                "Extremely Competitive",
                "https://example.com/weaker-cs",
                weakerMatch
        );

        when(universityRepository.filterUniversities("USA", 3.6, true, true, true))
                .thenReturn(List.of(weakerMatch, strongMatch));

        when(programRepository.findByUniversityId(strongMatch.getId()))
                .thenReturn(List.of(strongProgram));

        when(programRepository.findByUniversityId(weakerMatch.getId()))
                .thenReturn(List.of(weakerProgram));

        List<UniversitySearchResult> results = service.searchUniversities(
                "USA",
                3.6,
                true,
                true,
                true,
                "Computer Science"
        );

        assertEquals(2, results.size());
        assertEquals("Strong Match University", results.get(0).getName());
        assertTrue(results.get(0).getFitScore() >= results.get(1).getFitScore());

        assertNotNull(results.get(0).getTransferRequirementAnalysis());
        assertFalse(results.get(0).getTransferRequirementAnalysis().getAcademicSignals().isEmpty());
    }

    @Test
    void searchUniversitiesIncludesFitReasonsProgramDataAndRequirementAnalysis() {
        UniversityRepository universityRepository = mock(UniversityRepository.class);
        ProgramRepository programRepository = mock(ProgramRepository.class);
        TransferRequirementParser transferRequirementParser = new TransferRequirementParser();
        AiExplanationService aiExplanationService = new AiExplanationService();

        UniversityService service = new UniversityService(
                universityRepository,
                programRepository,
                transferRequirementParser,
                aiExplanationService
        );

        University university = new University(
                "Reason Test University",
                "USA",
                3.2,
                "Texas",
                "Austin",
                "May 1",
                "https://example.com/reason",
                true,
                true,
                false,
                "University application",
                "Calculus and programming courses preferred. Strong GPA and major preparation recommended."
        );

        Program program = new Program(
                "Computer Science",
                "Bachelor's",
                39000.0,
                3.3,
                "Competitive",
                "https://example.com/cs",
                university
        );

        when(universityRepository.filterUniversities("USA", 3.8, true, true, false))
                .thenReturn(List.of(university));

        when(programRepository.findByUniversityId(university.getId()))
                .thenReturn(List.of(program));

        List<UniversitySearchResult> results = service.searchUniversities(
                "USA",
                3.8,
                true,
                true,
                false,
                "Computer Science"
        );

        assertEquals(1, results.size());

        UniversitySearchResult result = results.get(0);

        assertFalse(result.getFitReasons().isEmpty());
        assertEquals("Computer Science", result.getMatchedMajor());
        assertEquals(39000.0, result.getEstimatedAnnualCost());
        assertEquals("Competitive", result.getProgramCompetitiveness());

        assertNotNull(result.getScoreBreakdown());
        assertTrue(result.getScoreBreakdown().getUniversityGpa() > 0);
        assertTrue(result.getScoreBreakdown().getProgramGpa() > 0);
        assertTrue(result.getScoreBreakdown().getMajorMatch() > 0);
        assertTrue(result.getScoreBreakdown().getCost() > 0);
        assertTrue(result.getFitScore() > 0);

        assertNotNull(result.getTransferRequirementAnalysis());
        assertTrue(
                result.getTransferRequirementAnalysis()
                        .getRecommendedCourses()
                        .contains("Calculus")
        );
        assertTrue(
                result.getTransferRequirementAnalysis()
                        .getRecommendedCourses()
                        .contains("Programming")
        );
        assertTrue(
                result.getTransferRequirementAnalysis()
                        .getSummary()
                        .contains("Recommends")
        );

        assertNotNull(result.getAiExplanation());
        assertNotNull(result.getAiExplanation().getSummary());
        assertFalse(result.getAiExplanation().getSummary().isBlank());
        assertFalse(result.getAiExplanation().getStrengths().isEmpty());
        assertFalse(result.getAiExplanation().getRisks().isEmpty());
        assertFalse(result.getAiExplanation().getNextSteps().isEmpty());

        assertTrue(
                result.getFitReasons().stream()
                        .anyMatch(reason -> reason.contains("University GPA contribution"))
        );

        assertTrue(
                result.getFitReasons().stream()
                        .anyMatch(reason -> reason.contains("Program GPA contribution"))
        );

        assertTrue(
                result.getFitReasons().stream()
                        .anyMatch(reason -> reason.contains("Major match contribution"))
        );

        assertTrue(
                result.getFitReasons().stream()
                        .anyMatch(reason -> reason.contains("Cost contribution"))
        );
    }
}