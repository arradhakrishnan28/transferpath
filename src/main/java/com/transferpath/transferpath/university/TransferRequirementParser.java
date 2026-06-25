package com.transferpath.transferpath.university;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TransferRequirementParser {

    private static final Map<String, String> COURSE_KEYWORDS = Map.ofEntries(
            Map.entry("calculus", "Calculus"),
            Map.entry("programming", "Programming"),
            Map.entry("computer science", "Computer Science"),
            Map.entry("data structures", "Data Structures"),
            Map.entry("discrete math", "Discrete Mathematics"),
            Map.entry("statistics", "Statistics"),
            Map.entry("linear algebra", "Linear Algebra"),
            Map.entry("physics", "Physics"),
            Map.entry("chemistry", "Chemistry"),
            Map.entry("english", "English Composition")
    );

    public TransferRequirementAnalysis analyze(String requirementText) {
        if (requirementText == null || requirementText.isBlank()) {
            return new TransferRequirementAnalysis(
                    List.of(),
                    List.of(),
                    List.of("No transfer requirement text was provided."),
                    List.of(),
                    "No requirements available yet."
            );
        }

        String normalizedText = requirementText.toLowerCase(Locale.ROOT);

        List<String> requiredCourses = findRequiredCourses(normalizedText);
        List<String> recommendedCourses = findRecommendedCourses(normalizedText);
        List<String> academicSignals = findAcademicSignals(normalizedText);
        List<String> applicationSignals = findApplicationSignals(normalizedText);

        String summary = buildSummary(
                requiredCourses,
                recommendedCourses,
                academicSignals,
                applicationSignals
        );

        return new TransferRequirementAnalysis(
                requiredCourses,
                recommendedCourses,
                academicSignals,
                applicationSignals,
                summary
        );
    }

    private List<String> findRequiredCourses(String text) {
        List<String> courses = new ArrayList<>();

        boolean hasRequiredLanguage = containsAny(
                text,
                "required",
                "must complete",
                "must have",
                "prerequisite",
                "prerequisites",
                "major preparation"
        );

        if (!hasRequiredLanguage) {
            return courses;
        }

        for (Map.Entry<String, String> course : COURSE_KEYWORDS.entrySet()) {
            if (text.contains(course.getKey())) {
                courses.add(course.getValue());
            }
        }

        return courses;
    }

    private List<String> findRecommendedCourses(String text) {
        List<String> courses = new ArrayList<>();

        boolean hasRecommendedLanguage = containsAny(
                text,
                "recommended",
                "preferred",
                "encouraged",
                "strongly suggested"
        );

        if (!hasRecommendedLanguage) {
            return courses;
        }

        for (Map.Entry<String, String> course : COURSE_KEYWORDS.entrySet()) {
            if (text.contains(course.getKey())) {
                courses.add(course.getValue());
            }
        }

        return courses;
    }

    private List<String> findAcademicSignals(String text) {
        List<String> signals = new ArrayList<>();

        if (containsAny(text, "strong gpa", "high gpa", "competitive gpa")) {
            signals.add("Strong GPA is emphasized.");
        }

        if (containsAny(text, "academic record", "academic performance")) {
            signals.add("Strong academic performance is important.");
        }

        if (containsAny(text, "rigorous", "rigor")) {
            signals.add("Course rigor is emphasized.");
        }

        if (containsAny(text, "college coursework", "transferable coursework")) {
            signals.add("Transferable college coursework is important.");
        }

        if (containsAny(text, "major preparation", "major-specific")) {
            signals.add("Major-specific preparation is important.");
        }

        if (signals.isEmpty()) {
            signals.add("General academic readiness is considered.");
        }

        return signals;
    }

    private List<String> findApplicationSignals(String text) {
        List<String> signals = new ArrayList<>();

        if (containsAny(text, "competitive transfer applicant", "competitive applicant")) {
            signals.add("The school describes transfer admission as competitive.");
        }

        if (containsAny(text, "common application", "applytexas", "uc application", "university application")) {
            signals.add("Application platform requirements may vary by school.");
        }

        if (containsAny(text, "deadline", "priority")) {
            signals.add("Deadline timing may affect application strength.");
        }

        return signals;
    }

    private String buildSummary(
            List<String> requiredCourses,
            List<String> recommendedCourses,
            List<String> academicSignals,
            List<String> applicationSignals
    ) {
        List<String> summaryParts = new ArrayList<>();

        if (!requiredCourses.isEmpty()) {
            summaryParts.add("Requires " + String.join(", ", requiredCourses) + ".");
        }

        if (!recommendedCourses.isEmpty()) {
            summaryParts.add("Recommends " + String.join(", ", recommendedCourses) + ".");
        }

        if (!academicSignals.isEmpty()) {
            summaryParts.add(academicSignals.get(0));
        }

        if (!applicationSignals.isEmpty()) {
            summaryParts.add(applicationSignals.get(0));
        }

        if (summaryParts.isEmpty()) {
            return "General transfer readiness is considered.";
        }

        return String.join(" ", summaryParts);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}