package com.transferpath.transferpath.university;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String majorName;
    private String degreeLevel;
    private Double estimatedAnnualCost;
    private Double minimumRecommendedGpa;
    private String competitivenessLevel;
    private String programUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    private University university;

    public Program() {
    }

    public Program(
            String majorName,
            String degreeLevel,
            Double estimatedAnnualCost,
            Double minimumRecommendedGpa,
            String competitivenessLevel,
            String programUrl,
            University university
    ) {
        this.majorName = majorName;
        this.degreeLevel = degreeLevel;
        this.estimatedAnnualCost = estimatedAnnualCost;
        this.minimumRecommendedGpa = minimumRecommendedGpa;
        this.competitivenessLevel = competitivenessLevel;
        this.programUrl = programUrl;
        this.university = university;
    }

    public Long getId() {
        return id;
    }

    public String getMajorName() {
        return majorName;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public Double getEstimatedAnnualCost() {
        return estimatedAnnualCost;
    }

    public Double getMinimumRecommendedGpa() {
        return minimumRecommendedGpa;
    }

    public String getCompetitivenessLevel() {
        return competitivenessLevel;
    }

    public String getProgramUrl() {
        return programUrl;
    }

    public University getUniversity() {
        return university;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public void setEstimatedAnnualCost(Double estimatedAnnualCost) {
        this.estimatedAnnualCost = estimatedAnnualCost;
    }

    public void setMinimumRecommendedGpa(Double minimumRecommendedGpa) {
        this.minimumRecommendedGpa = minimumRecommendedGpa;
    }

    public void setCompetitivenessLevel(String competitivenessLevel) {
        this.competitivenessLevel = competitivenessLevel;
    }

    public void setProgramUrl(String programUrl) {
        this.programUrl = programUrl;
    }

    public void setUniversity(University university) {
        this.university = university;
    }
}