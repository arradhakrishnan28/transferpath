package com.transferpath.transferpath.university;

import java.util.List;

public class UniversitySearchResult {

    private Long id;
    private String name;
    private String country;
    private String state;
    private String city;
    private Double minGpa;

    private Boolean acceptsInternational;
    private Boolean acceptsFall;
    private Boolean acceptsSpring;

    private String applicationDeadline;
    private String websiteUrl;
    private String applicationPortal;
    private String transferRequirements;
    private TransferRequirementAnalysis transferRequirementAnalysis;

    private Double fitScore;
    private ScoreBreakdown scoreBreakdown;
    private List<String> fitReasons;

    private String matchedMajor;
    private String matchedDegreeLevel;
    private Double estimatedAnnualCost;
    private Double minimumRecommendedProgramGpa;
    private String programCompetitiveness;
    private String programUrl;

    public UniversitySearchResult(
            Long id,
            String name,
            String country,
            String state,
            String city,
            Double minGpa,
            Boolean acceptsInternational,
            Boolean acceptsFall,
            Boolean acceptsSpring,
            String applicationDeadline,
            String websiteUrl,
            String applicationPortal,
            String transferRequirements,
            TransferRequirementAnalysis transferRequirementAnalysis,
            Double fitScore,
            ScoreBreakdown scoreBreakdown,
            List<String> fitReasons,
            String matchedMajor,
            String matchedDegreeLevel,
            Double estimatedAnnualCost,
            Double minimumRecommendedProgramGpa,
            String programCompetitiveness,
            String programUrl
    ) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.state = state;
        this.city = city;
        this.minGpa = minGpa;
        this.acceptsInternational = acceptsInternational;
        this.acceptsFall = acceptsFall;
        this.acceptsSpring = acceptsSpring;
        this.applicationDeadline = applicationDeadline;
        this.websiteUrl = websiteUrl;
        this.applicationPortal = applicationPortal;
        this.transferRequirements = transferRequirements;
        this.transferRequirementAnalysis = transferRequirementAnalysis;
        this.fitScore = fitScore;
        this.scoreBreakdown = scoreBreakdown;
        this.fitReasons = fitReasons;
        this.matchedMajor = matchedMajor;
        this.matchedDegreeLevel = matchedDegreeLevel;
        this.estimatedAnnualCost = estimatedAnnualCost;
        this.minimumRecommendedProgramGpa = minimumRecommendedProgramGpa;
        this.programCompetitiveness = programCompetitiveness;
        this.programUrl = programUrl;
    }

    public static UniversitySearchResult fromUniversity(
            University university,
            ScoreBreakdown scoreBreakdown,
            List<String> fitReasons,
            Program matchedProgram
    ) {
        return fromUniversity(university, scoreBreakdown, fitReasons, matchedProgram, null);
    }

    public static UniversitySearchResult fromUniversity(
            University university,
            ScoreBreakdown scoreBreakdown,
            List<String> fitReasons,
            Program matchedProgram,
            TransferRequirementAnalysis transferRequirementAnalysis
    ) {
        return new UniversitySearchResult(
                university.getId(),
                university.getName(),
                university.getCountry(),
                university.getState(),
                university.getCity(),
                university.getMinGpa(),
                university.getAcceptsInternational(),
                university.getAcceptsFall(),
                university.getAcceptsSpring(),
                university.getApplicationDeadline(),
                university.getWebsiteUrl(),
                university.getApplicationPortal(),
                university.getTransferRequirements(),
                transferRequirementAnalysis,
                roundScore(scoreBreakdown.total()),
                scoreBreakdown,
                fitReasons,
                matchedProgram == null ? null : matchedProgram.getMajorName(),
                matchedProgram == null ? null : matchedProgram.getDegreeLevel(),
                matchedProgram == null ? null : matchedProgram.getEstimatedAnnualCost(),
                matchedProgram == null ? null : matchedProgram.getMinimumRecommendedGpa(),
                matchedProgram == null ? null : matchedProgram.getCompetitivenessLevel(),
                matchedProgram == null ? null : matchedProgram.getProgramUrl()
        );
    }

    private static Double roundScore(Double score) {
        return Math.round(score * 10.0) / 10.0;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public Double getMinGpa() {
        return minGpa;
    }

    public Boolean getAcceptsInternational() {
        return acceptsInternational;
    }

    public Boolean getAcceptsFall() {
        return acceptsFall;
    }

    public Boolean getAcceptsSpring() {
        return acceptsSpring;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getApplicationPortal() {
        return applicationPortal;
    }

    public String getTransferRequirements() {
        return transferRequirements;
    }

    public TransferRequirementAnalysis getTransferRequirementAnalysis() {
        return transferRequirementAnalysis;
    }

    public Double getFitScore() {
        return fitScore;
    }

    public ScoreBreakdown getScoreBreakdown() {
        return scoreBreakdown;
    }

    public List<String> getFitReasons() {
        return fitReasons;
    }

    public String getMatchedMajor() {
        return matchedMajor;
    }

    public String getMatchedDegreeLevel() {
        return matchedDegreeLevel;
    }

    public Double getEstimatedAnnualCost() {
        return estimatedAnnualCost;
    }

    public Double getMinimumRecommendedProgramGpa() {
        return minimumRecommendedProgramGpa;
    }

    public String getProgramCompetitiveness() {
        return programCompetitiveness;
    }

    public String getProgramUrl() {
        return programUrl;
    }
}