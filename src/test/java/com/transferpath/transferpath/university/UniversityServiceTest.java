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

        UniversityService service = new UniversityService(universityRepository, programRepository);

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
                "Strong transfer profile"
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
                "Competitive transfer profile"
        );

        Program strongProgram = new Program(
                "Computer Science",
                "Bachelor's",
                45000.0,
                3.2,
                "Competitive",
                "https://example.com/strong-cs",
                strongMatch
        );

        Program weakerProgram = new Program(
                "Computer Science",
                "Bachelor's",
                45000.0,
                3.7,
                "Highly Competitive",
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
    }

    @Test
    void searchUniversitiesIncludesFitReasonsAndProgramData() {
        UniversityRepository universityRepository = mock(UniversityRepository.class);
        ProgramRepository programRepository = mock(ProgramRepository.class);

        UniversityService service = new UniversityService(universityRepository, programRepository);

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
                "Good academic standing"
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

        assertTrue(result.getFitReasons().contains("Student GPA is well above the university's listed minimum GPA"));
        assertTrue(result.getFitReasons().contains("Student GPA is well above the recommended GPA for the matched program"));
        assertTrue(result.getFitReasons().contains("Exact major match found: Computer Science"));
        assertTrue(result.getFitReasons().contains("Accepts international transfer students"));
        assertTrue(result.getFitReasons().contains("Supports fall transfer admission"));
    }
}