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

    private Double fitScore;
    private List<String> fitReasons;

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
            Double fitScore,
            List<String> fitReasons
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
        this.fitScore = fitScore;
        this.fitReasons = fitReasons;
    }

    public static UniversitySearchResult fromUniversity(
            University university,
            Double fitScore,
            List<String> fitReasons
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
                fitScore,
                fitReasons
        );
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

    public Double getFitScore() {
        return fitScore;
    }

    public List<String> getFitReasons() {
        return fitReasons;
    }
}