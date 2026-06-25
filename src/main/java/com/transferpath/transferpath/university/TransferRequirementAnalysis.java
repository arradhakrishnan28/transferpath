package com.transferpath.transferpath.university;

import java.util.List;

public class TransferRequirementAnalysis {

    private List<String> requiredCourses;
    private List<String> recommendedCourses;
    private List<String> academicSignals;
    private List<String> applicationSignals;
    private String summary;

    public TransferRequirementAnalysis(
            List<String> requiredCourses,
            List<String> recommendedCourses,
            List<String> academicSignals,
            List<String> applicationSignals,
            String summary
    ) {
        this.requiredCourses = requiredCourses;
        this.recommendedCourses = recommendedCourses;
        this.academicSignals = academicSignals;
        this.applicationSignals = applicationSignals;
        this.summary = summary;
    }

    public List<String> getRequiredCourses() {
        return requiredCourses;
    }

    public List<String> getRecommendedCourses() {
        return recommendedCourses;
    }

    public List<String> getAcademicSignals() {
        return academicSignals;
    }

    public List<String> getApplicationSignals() {
        return applicationSignals;
    }

    public String getSummary() {
        return summary;
    }
}