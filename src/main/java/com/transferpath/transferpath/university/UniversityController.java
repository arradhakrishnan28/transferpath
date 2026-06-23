package com.transferpath.transferpath.university;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universities")
public class UniversityController {

    private final UniversityRepository repository;
    private final UniversityService universityService;

    public UniversityController(
            UniversityRepository repository,
            UniversityService universityService
    ) {
        this.repository = repository;
        this.universityService = universityService;
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

    @GetMapping("/search/international")
    public List<University> searchByInternational(@RequestParam Boolean accepted) {
        return repository.findByAcceptsInternational(accepted);
    }

    @GetMapping("/search/fall")
    public List<University> searchByFall(@RequestParam Boolean accepted) {
        return repository.findByAcceptsFall(accepted);
    }

    @GetMapping("/search/spring")
    public List<University> searchBySpring(@RequestParam Boolean accepted) {
        return repository.findByAcceptsSpring(accepted);
    }

    @GetMapping("/search/filter")
    public List<UniversitySearchResult> filterUniversities(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double gpa,
            @RequestParam(required = false) Boolean international,
            @RequestParam(required = false) Boolean fall,
            @RequestParam(required = false) Boolean spring
    ) {
        return universityService.searchUniversities(country, gpa, international, fall, spring);
    }

    @PostMapping
    public University addUniversity(@RequestBody University university) {
        return repository.save(university);
    }
}