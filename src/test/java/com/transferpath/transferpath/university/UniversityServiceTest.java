package com.transferpath.transferpath.university;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UniversityServiceTest {

    @Test
    void searchUniversitiesReturnsResultsSortedByFitScoreDescending() {
        UniversityRepository repository = mock(UniversityRepository.class);
        UniversityService service = new UniversityService(repository);

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

        when(repository.filterUniversities("USA", 3.6, true, true, true))
                .thenReturn(List.of(weakerMatch, strongMatch));

        List<UniversitySearchResult> results = service.searchUniversities(
                "USA",
                3.6,
                true,
                true,
                true
        );

        assertEquals(2, results.size());
        assertEquals("Strong Match University", results.get(0).getName());
        assertTrue(results.get(0).getFitScore() >= results.get(1).getFitScore());
    }

    @Test
    void searchUniversitiesIncludesFitReasons() {
        UniversityRepository repository = mock(UniversityRepository.class);
        UniversityService service = new UniversityService(repository);

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

        when(repository.filterUniversities("USA", 3.8, true, true, false))
                .thenReturn(List.of(university));

        List<UniversitySearchResult> results = service.searchUniversities(
                "USA",
                3.8,
                true,
                true,
                false
        );

        assertEquals(1, results.size());
        assertFalse(results.get(0).getFitReasons().isEmpty());
        assertTrue(results.get(0).getFitReasons().contains("Student GPA is well above the listed minimum GPA"));
        assertTrue(results.get(0).getFitReasons().contains("Accepts international transfer students"));
        assertTrue(results.get(0).getFitReasons().contains("Supports fall transfer admission"));
    }
}