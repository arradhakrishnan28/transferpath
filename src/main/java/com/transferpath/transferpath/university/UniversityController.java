package com.transferpath.transferpath.university;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universities")
public class UniversityController {

    private final UniversityRepository repository;

    public UniversityController(UniversityRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<University> getUniversities() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public University getUniversityById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/search")
    public List<University> searchByCountry(@RequestParam String country) {
        return repository.findByCountry(country);
    }

    @GetMapping("/search/gpa")
    public List<University> searchByGpa(@RequestParam Double gpa) {
        return repository.findByMinGpaLessThanEqual(gpa);
    }

    @GetMapping("/search/basic")
    public List<University> basicSearch(@RequestParam String country, @RequestParam Double gpa) {
        return repository.findByCountryAndMinGpaLessThanEqual(country, gpa);
    }

    @PostMapping
    public University addUniversity(@RequestBody University university) {
        return repository.save(university);
    }
}