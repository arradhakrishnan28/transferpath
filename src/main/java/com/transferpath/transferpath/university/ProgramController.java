package com.transferpath.transferpath.university;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/programs")
public class ProgramController {

    private final ProgramRepository programRepository;

    public ProgramController(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    @GetMapping
    public List<Program> getPrograms() {
        return programRepository.findAll();
    }

    @GetMapping("/university/{universityId}")
    public List<Program> getProgramsByUniversity(@PathVariable Long universityId) {
        return programRepository.findByUniversityId(universityId);
    }

    @GetMapping("/search")
    public List<Program> searchProgramsByMajor(@RequestParam String major) {
        return programRepository.findByMajorNameContainingIgnoreCase(major);
    }

    @GetMapping("/search/gpa")
    public List<Program> searchProgramsByMajorAndGpa(
            @RequestParam String major,
            @RequestParam Double gpa
    ) {
        return programRepository.findByMajorNameContainingIgnoreCaseAndMinimumRecommendedGpaLessThanEqual(
                major,
                gpa
        );
    }
}