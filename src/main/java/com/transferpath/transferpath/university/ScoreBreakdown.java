package com.transferpath.transferpath.university;

public class ScoreBreakdown {

    private Double universityGpa;
    private Double programGpa;
    private Double majorMatch;
    private Double international;
    private Double termMatch;
    private Double cost;
    private Double competitiveness;

    public ScoreBreakdown(
            Double universityGpa,
            Double programGpa,
            Double majorMatch,
            Double international,
            Double termMatch,
            Double cost,
            Double competitiveness
    ) {
        this.universityGpa = universityGpa;
        this.programGpa = programGpa;
        this.majorMatch = majorMatch;
        this.international = international;
        this.termMatch = termMatch;
        this.cost = cost;
        this.competitiveness = competitiveness;
    }

    public Double getUniversityGpa() {
        return universityGpa;
    }

    public Double getProgramGpa() {
        return programGpa;
    }

    public Double getMajorMatch() {
        return majorMatch;
    }

    public Double getInternational() {
        return international;
    }

    public Double getTermMatch() {
        return termMatch;
    }

    public Double getCost() {
        return cost;
    }

    public Double getCompetitiveness() {
        return competitiveness;
    }

    public Double total() {
        return universityGpa
                + programGpa
                + majorMatch
                + international
                + termMatch
                + cost
                + competitiveness;
    }
}