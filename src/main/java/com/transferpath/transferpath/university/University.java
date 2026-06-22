package com.transferpath.transferpath.university;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    private Double minGpa;

    private String state;
    private String city;
    private String applicationDeadline;
    private String websiteUrl;

    private Boolean acceptsInternational;
    private Boolean acceptsFall;
    private Boolean acceptsSpring;
    private String applicationPortal;
    private String transferRequirements;

    public University() {
    }

    public University(String name,
                      String country,
                      Double minGpa,
                      String state,
                      String city,
                      String applicationDeadline,
                      String websiteUrl,
                      Boolean acceptsInternational,
                      Boolean acceptsFall,
                      Boolean acceptsSpring,
                      String applicationPortal,
                      String transferRequirements) {

        this.name = name;
        this.country = country;
        this.minGpa = minGpa;
        this.state = state;
        this.city = city;
        this.applicationDeadline = applicationDeadline;
        this.websiteUrl = websiteUrl;

        this.acceptsInternational = acceptsInternational;
        this.acceptsFall = acceptsFall;
        this.acceptsSpring = acceptsSpring;
        this.applicationPortal = applicationPortal;
        this.transferRequirements = transferRequirements;
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

    public Double getMinGpa() {
        return minGpa;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setMinGpa(Double minGpa) {
        this.minGpa = minGpa;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Boolean getAcceptsInternational() {
        return acceptsInternational;
    }

    public void setAcceptsInternational(Boolean acceptsInternational) {
        this.acceptsInternational = acceptsInternational;
    }

    public Boolean getAcceptsFall() {
        return acceptsFall;
    }

    public void setAcceptsFall(Boolean acceptsFall) {
        this.acceptsFall = acceptsFall;
    }

    public Boolean getAcceptsSpring() {
        return acceptsSpring;
    }

    public void setAcceptsSpring(Boolean acceptsSpring) {
        this.acceptsSpring = acceptsSpring;
    }

    public String getApplicationPortal() {
        return applicationPortal;
    }

    public void setApplicationPortal(String applicationPortal) {
        this.applicationPortal = applicationPortal;
    }

    public String getTransferRequirements() {
        return transferRequirements;
    }

    public void setTransferRequirements(String transferRequirements) {
        this.transferRequirements = transferRequirements;
    }

}