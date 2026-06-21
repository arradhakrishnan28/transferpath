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

    public University() {
    }

    public University(String name, String country, Double minGpa) {
        this.name = name;
        this.country = country;
        this.minGpa = minGpa;
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
}