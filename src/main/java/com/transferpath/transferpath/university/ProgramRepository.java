package com.transferpath.transferpath.university;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findByUniversityId(Long universityId);

    List<Program> findByMajorNameContainingIgnoreCase(String majorName);

    List<Program> findByMajorNameContainingIgnoreCaseAndMinimumRecommendedGpaLessThanEqual(
            String majorName,
            Double gpa
    );
}